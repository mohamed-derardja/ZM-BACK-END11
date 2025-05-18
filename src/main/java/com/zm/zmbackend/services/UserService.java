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
    Page<Car> getAllAvailableCarsPaged(Pageable pageable);

    List<Car> getCarsByBrand(String brand);
    Page<Car> getCarsByBrandPaged(String brand, Pageable pageable);

    List<Car> getCarsByModel(String model);
    Page<Car> getCarsByModelPaged(String model, Pageable pageable);

    List<Car> getCarsByRatingRange(Long minRating, Long maxRating);
    Page<Car> getCarsByRatingRangePaged(Long minRating, Long maxRating, Pageable pageable);

    // Reservation management
    Reservation createReservation(Reservation reservation, Long currentUserId);
    Reservation cancelReservation(Long reservationId, Long currentUserId);

    List<Reservation> getUpcomingReservations(Long userId, Long currentUserId);
    Page<Reservation> getUpcomingReservationsPaged(Long userId, Long currentUserId, Pageable pageable);

    List<Reservation> getPastReservations(Long userId, Long currentUserId);
    Page<Reservation> getPastReservationsPaged(Long userId, Long currentUserId, Pageable pageable);

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
    Page<Payment> getAllPaymentsPaged(Pageable pageable);

    Optional<Payment> getPaymentById(Long id);

    List<Payment> getPaymentsByUserId(Long userId);
    Page<Payment> getPaymentsByUserIdPaged(Long userId, Pageable pageable);

    List<Payment> getPaymentsByReservation(Reservation reservation);
    Page<Payment> getPaymentsByReservationPaged(Reservation reservation, Pageable pageable);

    Payment createPayment(Payment payment);
    Payment createReservationPayment(Reservation reservation, BigDecimal amount);
    Payment createReservationPayment(Reservation reservation, BigDecimal amount, PaymentMethodType paymentMethod);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}
