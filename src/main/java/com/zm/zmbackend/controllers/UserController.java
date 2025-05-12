package com.zm.zmbackend.controllers;

import com.zm.zmbackend.dto.LoginRequest;
import com.zm.zmbackend.dto.LoginResponse;
import com.zm.zmbackend.dto.VerificationRequest;
import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.entities.User;
import com.zm.zmbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Authentication endpoints

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userService.getUserByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty() || !userService.verifyPassword(loginRequest.getPassword(), userOpt.get().getPassword())) {
                return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }

            User user = userOpt.get();
            String token = userService.generateToken(user.getId());

            LoginResponse response = new LoginResponse(
                user.getId(),
                token,
                user.getEmailVerified(),
                user.getPhoneVerified()
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Check if email already exists
            Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return new ResponseEntity<>("Email already in use", HttpStatus.CONFLICT);
            }

            // Set verification status to false
            user.setEmailVerified(false);
            user.setPhoneVerified(false);

            User savedUser = userService.createUser(user);

            // Generate token for the new user
            String token = userService.generateToken(savedUser.getId());

            LoginResponse response = new LoginResponse(
                savedUser.getId(),
                token,
                savedUser.getEmailVerified(),
                savedUser.getPhoneVerified()
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{userId}/verify-email")
    public ResponseEntity<?> verifyEmail(@PathVariable Long userId, 
                                        @RequestBody VerificationRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null || !currentUserId.equals(userId)) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            boolean verified = userService.verifyEmail(userId, request.getVerificationCode());
            if (!verified) {
                return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("Email verified successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{userId}/verify-phone")
    public ResponseEntity<?> verifyPhone(@PathVariable Long userId, 
                                        @RequestBody VerificationRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null || !currentUserId.equals(userId)) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            boolean verified = userService.verifyPhone(userId, request.getVerificationCode());
            if (!verified) {
                return new ResponseEntity<>("Invalid verification code", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("Phone verified successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // User management endpoints

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Authorization check: users can only view their own profile
            if (!id.equals(currentUserId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Optional<User> user = userService.getUserById(id);
            return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user, 
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            User updatedUser = userService.updateUser(id, user, currentUserId);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            userService.deleteUser(id, currentUserId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Car browsing endpoints

    @GetMapping("/cars")
    public ResponseEntity<List<Car>> getAllAvailableCars() {
        List<Car> cars = userService.getAllAvailableCars();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/cars/brand/{brand}")
    public ResponseEntity<List<Car>> getCarsByBrand(@PathVariable String brand) {
        List<Car> cars = userService.getCarsByBrand(brand);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/cars/model/{model}")
    public ResponseEntity<List<Car>> getCarsByModel(@PathVariable String model) {
        List<Car> cars = userService.getCarsByModel(model);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/cars/rating")
    public ResponseEntity<List<Car>> getCarsByRatingRange(@RequestParam Long minRating, @RequestParam Long maxRating) {
        List<Car> cars = userService.getCarsByRatingRange(minRating, maxRating);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    // Reservation management endpoints

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation, 
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Reservation createdReservation = userService.createReservation(reservation, currentUserId);
            return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Rate limit exceeded")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
            } else if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            } else if (e.getMessage().contains("overlapping")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reservations/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            Reservation cancelledReservation = userService.cancelReservation(id, currentUserId);
            return new ResponseEntity<>(cancelledReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/reservations/upcoming")
    public ResponseEntity<List<Reservation>> getUpcomingReservations(@PathVariable Long userId, 
                                                                    @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Reservation> reservations = userService.getUpcomingReservations(userId, currentUserId);
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/reservations/past")
    public ResponseEntity<List<Reservation>> getPastReservations(@PathVariable Long userId, 
                                                               @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header
            String token = authHeader;
            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Validate token
            Long currentUserId = userService.validateToken(token);
            if (currentUserId == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            List<Reservation> reservations = userService.getPastReservations(userId, currentUserId);
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
