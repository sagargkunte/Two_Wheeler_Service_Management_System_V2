package com.twowheeler.controller;

import com.twowheeler.dto.RequestDtos.*;
import com.twowheeler.model.*;
import com.twowheeler.repository.*;
import com.twowheeler.service.DistanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MechanicRepository mechanicRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DistanceService distanceService;

    private User getLoggedInUser(HttpSession session) {
        Long userId = (Long) session.getAttribute(AuthController.SESSION_USER_ID);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "API is working!"));
    }

    @   GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("_id", user.getId().toString());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("type", user.getType());

        return ResponseEntity.ok(userData);
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentRequest request, HttpSession session) {
        try {
            User user = getLoggedInUser(session);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }

            if (request.getMechanicId() == null || request.getBikeId() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mechanic ID and Bike ID are required"));
            }

            Optional<Bike> bikeOpt = bikeRepository.findById(request.getBikeId());
            Optional<Mechanic> mechanicOpt = mechanicRepository.findById(request.getMechanicId());

            if (bikeOpt.isEmpty() || mechanicOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Bike or Mechanic not found"));
            }

            Bike bike = bikeOpt.get();
            Mechanic mechanic = mechanicOpt.get();

            String appointmentDate = request.getDate() != null && !request.getDate().isBlank()
                    ? request.getDate()
                    : java.time.LocalDate.now().toString();

            String appointmentTime = request.getTime() != null && !request.getTime().isBlank()
                    ? request.getTime()
                    : "10:00 AM";

            Appointment appointment = new Appointment(
                    bike,
                    mechanic,
                    appointmentDate,
                    appointmentTime,
                    request.getNotes() != null ? request.getNotes() : ""
            );

            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Link bike to appointment
            bike.setMechanicId(mechanic);
            bike.setAppointmentId(savedAppointment.getId());
            bikeRepository.save(bike);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Appointment created successfully",
                    "appointment", savedAppointment
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to create appointment: " + e.getMessage()));
        }
    }

    @GetMapping("/mechanic-appointments")
    public ResponseEntity<?> getMechanicAppointments(HttpSession session) {
        try {
            User user = getLoggedInUser(session);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }

            Optional<Mechanic> mechanicOpt = mechanicRepository.findByUserId_Id(user.getId());
            if (mechanicOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Mechanic profile not found"));
            }

            Mechanic mechanic = mechanicOpt.get();
            List<Appointment> appointments = appointmentRepository.findByMechanicIdOrderByCreatedAtDesc(mechanic);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "appointments", appointments,
                    "mechanic", mechanic
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch appointments", "error", e.getMessage()));
        }
    }

    @GetMapping("/owner-dashboard")
    public ResponseEntity<?> getOwnerDashboard(HttpSession session) {
        try {
            User user = getLoggedInUser(session);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
            }

            List<Bike> bikes = bikeRepository.findByUserId_IdOrderByCreatedAtDesc(user.getId());
            List<Appointment> appointments = bikes.isEmpty()
                    ? Collections.emptyList()
                    : appointmentRepository.findByBikeIdInOrderByCreatedAtDesc(bikes);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "bikes", bikes,
                    "appointments", appointments
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch owner dashboard data", "error", e.getMessage()));
        }
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> getAppointmentDetails(@PathVariable Long appointmentId) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Appointment not found"));
            }

            Appointment appointment = appointmentOpt.get();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "appointment", appointment,
                    "bike", appointment.getBikeId(),
                    "mechanic", appointment.getMechanicId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch appointment"));
        }
    }

    @PutMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long appointmentId, @RequestBody UpdateAppointmentRequest request) {
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Appointment not found"));
            }

            Appointment appointment = appointmentOpt.get();

            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                appointment.setStatus(request.getStatus());

                // Update bike status correspondingly
                Bike bike = appointment.getBikeId();
                if (bike != null) {
                    if ("bike-received".equals(request.getStatus()) || "under-repair".equals(request.getStatus())) {
                        bike.setStatus("in-repair");
                    } else if ("completed".equals(request.getStatus())) {
                        bike.setStatus("ready-for-delivery");
                    }
                    bikeRepository.save(bike);
                }
            }

            if (request.getEstimatedCompletion() != null) {
                appointment.setEstimatedCompletion(request.getEstimatedCompletion());
            }

            if (request.getMechanicNotes() != null) {
                appointment.setMechanicNotes(request.getMechanicNotes());
            }

            Appointment updated = appointmentRepository.save(appointment);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment updated successfully",
                    "appointment", updated
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update appointment"));
        }
    }

    @GetMapping("/bikes/{bikeId}")
    public ResponseEntity<?> getBikeById(@PathVariable Long bikeId) {
        try {
            Optional<Bike> bikeOpt = bikeRepository.findById(bikeId);
            if (bikeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Bike not found"));
            }
            return ResponseEntity.ok(bikeOpt.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to fetch bike"));
        }
    }

    @PostMapping("/directions")
    public ResponseEntity<?> getDirections(@RequestBody DirectionsRequest request) {
        try {
            if (request.getSource() == null || request.getDestination() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Source and destination coordinates are required"));
            }

            double startLat = request.getSource().getLat();
            double startLon = request.getSource().getLon();
            double endLat = request.getDestination().getLat();
            double endLon = request.getDestination().getLon();

            Map<String, Object> directions = distanceService.getRoadDistance(startLat, startLon, endLat, endLon);

            return ResponseEntity.ok(directions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch directions: " + e.getMessage()));
        }
    }
}
