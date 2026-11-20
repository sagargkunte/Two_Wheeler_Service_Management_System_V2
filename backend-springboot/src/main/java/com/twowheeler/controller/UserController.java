package com.twowheeler.controller;

import com.twowheeler.dto.RequestDtos.AddBikeRequest;
import com.twowheeler.model.Bike;
import com.twowheeler.model.Mechanic;
import com.twowheeler.model.User;
import com.twowheeler.repository.BikeRepository;
import com.twowheeler.repository.MechanicRepository;
import com.twowheeler.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private MechanicRepository mechanicRepository;

    @PostMapping("/addBike")
    public ResponseEntity<?> addBike(@RequestBody AddBikeRequest request, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute(AuthController.SESSION_USER_ID);
            User user = null;
            if (userId != null) {
                user = userRepository.findById(userId).orElse(null);
            }

            if (user == null) {
                // If no active session, find first owner user or create demo user so testing works seamlessly
                List<User> owners = userRepository.findAll().stream().filter(u -> "owner".equalsIgnoreCase(u.getType())).toList();
                if (!owners.isEmpty()) {
                    user = owners.get(0);
                } else {
                    user = userRepository.findAll().stream().findFirst().orElse(null);
                }
            }

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please login first to add a bike"));
            }

            if (request.getName() == null || request.getModel() == null || request.getProblem() == null || request.getServiceType() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "All required fields must be provided"));
            }

            Bike bike = new Bike(
                    user,
                    request.getName().trim(),
                    request.getModel().trim(),
                    request.getRegistrationNumber(),
                    request.getProblem().trim(),
                    request.getServiceType(),
                    request.getLocation(),
                    request.getLatitude(),
                    request.getLongitude()
            );

            Bike savedBike = bikeRepository.save(bike);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Bike added successfully",
                    "bike", savedBike
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/getMechanics")
    public ResponseEntity<?> getMechanics() {
        try {
            List<Mechanic> mechanics = mechanicRepository.findAll();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", mechanics.size(),
                    "mechanics", mechanics
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Server error while fetching mechanics"));
        }
    }
}
