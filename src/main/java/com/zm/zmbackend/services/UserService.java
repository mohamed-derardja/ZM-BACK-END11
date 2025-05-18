package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.PaymentMethodType;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserService {
    // Basic CRUD operations with authorization
    List<User> getAllUsers();
    Page<User> getAllUsersPaged(Pageable pageable);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User user, Long currentUserId);
    void deleteUser(Long id, Long currentUserId);

    // Car browsing with filtering
    List<Car> getAllAvailableCars();
    List<Car> getCarsByBrand(String brand);
    List<Car> getCarsByModel(String model);
    List<Car> getCarsByRatingRange(Long minRating, Long maxRating);

    // Reservation management
    Reservation createReservation(Reservation reservation, Long currentUserId);
    Reservation cancelReservation(Long reservationId, Long currentUserId);
    List<Reservation> getUpcomingReservations(Long userId, Long currentUserId);
    List<Reservation> getPastReservations(Long userId, Long currentUserId);

    // Authentication and validation
    boolean isAuthenticated(Long userId);
    boolean isEmailVerified(Long userId);

    // Verification
    boolean verifyEmail(Long userId, String verificationCode);
    String generateEmailVerificationCode(Long userId);

    // Token management
    String generateToken(Long userId);
    Long validateToken(String token);
    void invalidateToken(Long userId);

    // Rate limiting
    boolean checkReservationRateLimit(Long userId);

    // Password verification
    boolean verifyPassword(String rawPassword, String encodedPassword);

    // Payment methods (integrated from PaymentService)
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getPaymentsByUserId(Long userId);
    List<Payment> getPaymentsByReservation(Reservation reservation);
    Payment createPayment(Payment payment);
    Payment createReservationPayment(Reservation reservation, BigDecimal amount);
    Payment createReservationPayment(Reservation reservation, BigDecimal amount, PaymentMethodType paymentMethod);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}
