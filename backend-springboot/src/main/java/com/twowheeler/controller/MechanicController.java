package com.twowheeler.controller;

import com.twowheeler.dto.RequestDtos.MechanicProfileRequest;
import com.twowheeler.model.Mechanic;
import com.twowheeler.model.User;
import com.twowheeler.repository.MechanicRepository;
import com.twowheeler.repository.UserRepository;
import com.twowheeler.service.DistanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MechanicController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MechanicRepository mechanicRepository;

    @Autowired
    private DistanceService distanceService;

    @PostMapping("/mechanic/profile")
    public ResponseEntity<?> saveMechanicProfile(@RequestBody MechanicProfileRequest request, HttpSession session) {
        try {
            String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : (String) session.getAttribute("otpEmail");
            if (email == null) {
                Long sessionUserId = (Long) session.getAttribute(AuthController.SESSION_USER_ID);
                if (sessionUserId != null) {
                    User u = userRepository.findById(sessionUserId).orElse(null);
                    if (u != null) email = u.getEmail();
                }
            }

            if (email == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is required"));
            }

            if (request.getShopName() == null || request.getAddress() == null || request.getLocation() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Missing required fields"));
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "User not found"));
            }

            User user = userOpt.get();
            Optional<Mechanic> existingProfile = mechanicRepository.findByUserId_Id(user.getId());

            Mechanic mechanic = existingProfile.orElse(new Mechanic());
            mechanic.setUserId(user);
            mechanic.setShopName(request.getShopName().trim());
            mechanic.setAddress(request.getAddress().trim());
            mechanic.setLatitude(request.getLocation().getLatitude());
            mechanic.setLongitude(request.getLocation().getLongitude());

            String servicesStr = request.getServicesOffered() != null
                    ? String.join(", ", request.getServicesOffered())
                    : "";
            mechanic.setServicesOffered(servicesStr);
            mechanic.setDescription(request.getDescription());

            Mechanic saved = mechanicRepository.save(mechanic);

            // Set login session
            session.setAttribute(AuthController.SESSION_USER_ID, user.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Mechanic profile saved successfully",
                    "mechanicProfile", saved
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error while saving mechanic profile: " + e.getMessage()));
        }
    }

    @GetMapping("/mechanic/{id}")
    public ResponseEntity<?> getMechanicDetails(@PathVariable Long id) {
        try {
            Optional<Mechanic> mechanicOpt = mechanicRepository.findById(id);
            if (mechanicOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Mechanic not found"));
            }
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mechanic", mechanicOpt.get()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error while fetching mechanic details"));
        }
    }

    @GetMapping("/getNearbyMechanics")
    public ResponseEntity<?> getNearbyMechanics(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        try {
            List<Mechanic> mechanics = mechanicRepository.findAll();

            if (latitude != null && longitude != null) {
                for (Mechanic m : mechanics) {
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        Map<String, Object> distInfo = distanceService.getRoadDistance(latitude, longitude, m.getLatitude(), m.getLongitude());
                        m.setDistance(((Number) distInfo.get("distance")).doubleValue());
                        m.setDuration(((Number) distInfo.get("duration")).doubleValue());
                    } else {
                        m.setDistance(999.0);
                        m.setDuration(999.0);
                    }
                }
                // Sort by distance ascending
                mechanics.sort(Comparator.comparingDouble(m -> m.getDistance() != null ? m.getDistance() : Double.MAX_VALUE));
            }

            return ResponseEntity.ok(Map.of("mechanics", mechanics));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error fetching nearby mechanics: " + e.getMessage()));
        }
    }

    @PostMapping("/book-appointment/{appointmentId}")
    public ResponseEntity<?> bookAppointmentDirect(@PathVariable Long appointmentId, @RequestBody Map<String, Object> payload) {
        try {
            String email = (String) payload.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email is required"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Booking confirmed"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Internal server error"));
        }
    }
}
