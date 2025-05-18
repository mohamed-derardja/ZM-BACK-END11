package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.PaymentMethodType;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.entities.User;
import com.zm.zmbackend.config.CancellationConfig;
import com.zm.zmbackend.exceptions.AuthenticationException;
import com.zm.zmbackend.exceptions.AuthorizationException;
import com.zm.zmbackend.exceptions.BusinessRuleViolationException;
import com.zm.zmbackend.exceptions.RateLimitExceededException;
import com.zm.zmbackend.exceptions.ResourceNotFoundException;
import com.zm.zmbackend.exceptions.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.zm.zmbackend.repositories.CarRepo;
import com.zm.zmbackend.repositories.PaymentRepo;
import com.zm.zmbackend.repositories.UserRepo;
import com.zm.zmbackend.services.CarService;
import com.zm.zmbackend.services.EmailService;
import com.zm.zmbackend.services.ReservationService;
import com.zm.zmbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final CarRepo carRepo;
    private final PaymentRepo paymentRepo;
    private final CarService carService;
    private final ReservationService reservationService;
    private final CancellationConfig cancellationConfig;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // For rate limiting: userId -> list of reservation attempt timestamps
    private final Map<Long, List<Instant>> reservationAttempts = new ConcurrentHashMap<>();

    // Constants
    private static final int MAX_RESERVATION_ATTEMPTS = 5;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 1;
    private static final int MAX_RESERVATION_DAYS = 90;

    // Constants for verification code generation
    private static final int VERIFICATION_CODE_LENGTH = 4;

    // Method to generate a random verification code
    private String generateVerificationCode() {
        return String.format("%0" + VERIFICATION_CODE_LENGTH + "d", new Random().nextInt((int) Math.pow(10, VERIFICATION_CODE_LENGTH)));
    }

    @Autowired
    public UserServiceImpl(UserRepo userRepo, CarRepo carRepo, PaymentRepo paymentRepo,
                          CarService carService, ReservationService reservationService,
                          CancellationConfig cancellationConfig, PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.userRepo = userRepo;
        this.carRepo = carRepo;
        this.paymentRepo = paymentRepo;
        this.carService = carService;
        this.reservationService = reservationService;
        this.cancellationConfig = cancellationConfig;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Basic CRUD operations with authorization

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Page<User> getAllUsersPaged(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User createUser(User user) {
        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set creation and update timestamps
        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return userRepo.save(user);
    }

    @Override
    public User updateUser(Long id, User user, Long currentUserId) {
        // Authorization check: users can only update their own account
        if (!id.equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only update your own account");
        }

        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        // Preserve creation timestamp and other fields if needed
        Optional<User> existingUserOpt = userRepo.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            user.setCreatedAt(existingUser.getCreatedAt());

            // Hash the password if it's different from the existing one
            if (user.getPassword() != null && !user.getPassword().isEmpty() && 
                !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // Keep the existing hashed password
                user.setPassword(existingUser.getPassword());
            }
        }

        // Update the timestamp
        user.setUpdatedAt(Instant.now());
        user.setId(id);
        return userRepo.save(user);
    }

    @Override
    public void deleteUser(Long id, Long currentUserId) {
        // Authorization check: users can only delete their own account
        if (!id.equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only delete your own account");
        }

        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepo.deleteById(id);
    }

    // Car browsing with filtering

    @Override
    public List<Car> getAllAvailableCars() {
        return carService.getAvailableCars();
    }

    @Override
    public Page<Car> getAllAvailableCarsPaged(Pageable pageable) {
        return carService.getAvailableCarsPaged(pageable);
    }

    @Override
    public List<Car> getCarsByBrand(String brand) {
        return carRepo.findByBrand(brand);
    }

    @Override
    public Page<Car> getCarsByBrandPaged(String brand, Pageable pageable) {
        return carService.getCarsByBrandPaged(brand, pageable);
    }

    @Override
    public List<Car> getCarsByModel(String model) {
        return carRepo.findByModel(model);
    }

    @Override
    public Page<Car> getCarsByModelPaged(String model, Pageable pageable) {
        return carService.getCarsByModelPaged(model, pageable);
    }

    @Override
    public List<Car> getCarsByRatingRange(Long minRating, Long maxRating) {
        return carRepo.findByRatingBetween(minRating, maxRating);
    }

    @Override
    public Page<Car> getCarsByRatingRangePaged(Long minRating, Long maxRating, Pageable pageable) {
        return carService.getCarsByRatingRangePaged(minRating, maxRating, pageable);
    }

    // Reservation management

    @Override
    public Reservation createReservation(Reservation reservation, Long currentUserId) {
        // Check if user is authenticated
        if (!isAuthenticated(currentUserId)) {
            throw new AuthenticationException("Unauthorized: User must be authenticated");
        }

        // Check if user's email is verified (optional requirement)
        if (!isEmailVerified(currentUserId)) {
            throw new AuthenticationException("Email verification required before making a reservation");
        }

        // Check rate limit
        if (!checkReservationRateLimit(currentUserId)) {
            throw new RateLimitExceededException("Rate limit exceeded: Please try again later");
        }

        // Authorization check: users can only create reservations for themselves
        if (!reservation.getUser().getId().equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only create reservations for yourself");
        }

        // Validate reservation dates
        if (!reservationService.validateReservationDates(reservation.getStartDate(), reservation.getEndDate())) {
            throw new ValidationException("Invalid reservation dates: Reservations must be for present or future dates and cannot exceed " + MAX_RESERVATION_DAYS + " days");
        }

        // Check car availability
        if (!reservationService.checkCarAvailability(reservation.getCar().getId(), reservation.getStartDate(), reservation.getEndDate())) {
            throw new BusinessRuleViolationException("Car is not available for the selected dates: There is an overlapping reservation");
        }

        // Set creation and update timestamps
        Instant now = Instant.now();
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);

        // Create the reservation
        return reservationService.createReservation(reservation);
    }

    @Override
    public Reservation cancelReservation(Long reservationId, Long currentUserId) {
        // Check if user is authenticated
        if (!isAuthenticated(currentUserId)) {
            throw new AuthenticationException("Unauthorized: User must be authenticated");
        }

        // Check if user's email is verified (optional requirement)
        if (!isEmailVerified(currentUserId)) {
            throw new AuthenticationException("Email verification required before cancelling a reservation");
        }

        // Get the reservation
        Optional<Reservation> optionalReservation = reservationService.getReservationById(reservationId);
        if (optionalReservation.isEmpty()) {
            throw new ResourceNotFoundException("Reservation", "id", reservationId);
        }

        Reservation reservation = optionalReservation.get();

        // Authorization check: users can only cancel their own reservations
        if (!reservation.getUser().getId().equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only cancel your own reservations");
        }

        // Check if the reservation has already started or ended
        Instant now = Instant.now();
        if (reservation.getStartDate().isBefore(now)) {
            throw new BusinessRuleViolationException("Cannot cancel a reservation that has already started or ended");
        }

        // Calculate cancellation fee based on how close to the start time
        BigDecimal feePercentage;
        feePercentage = cancellationConfig.calculateCancellationFeePercentage(reservation.getStartDate(), now);

        BigDecimal cancellationFee = reservation.getFee().multiply(feePercentage);

        // Update reservation status to "Cancelled" and set the updated timestamp
        reservation.setStatus("Cancelled");
        reservation.setUpdatedAt(now);

        // Store the cancellation fee in the reservation
        reservation.setCancellationFee(cancellationFee);

        // Use the updateReservation method to save all fields including the cancellation fee
        // This will also handle updating car and driver status
        return reservationService.updateReservation(reservationId, reservation);
    }

    @Override
    public List<Reservation> getUpcomingReservations(Long userId, Long currentUserId) {
        // Authorization check: users can only view their own reservations
        if (!userId.equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only view your own reservations");
        }

        // Get all reservations for the user
        List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

        // Filter for upcoming reservations (start date is in the future)
        Instant now = Instant.now();
        return userReservations.stream()
            .filter(r -> r.getStartDate().isAfter(now))
            .toList();
    }

    @Override
    public Page<Reservation> getUpcomingReservationsPaged(Long userId, Long currentUserId, Pageable pageable) {
        // Authorization check: users can only view their own reservations
        if (!userId.equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only view your own reservations");
        }

        // Get all reservations for the user and filter in memory
        // Note: This is not optimal for large datasets as it loads all data first
        // A better approach would be to add a custom query method in the repository
        List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

        // Filter for upcoming reservations (start date is in the future)
        Instant now = Instant.now();
        List<Reservation> upcomingReservations = userReservations.stream()
            .filter(r -> r.getStartDate().isAfter(now))
            .toList();

        // Get the paginated result from the filtered list
        // This is a simplified approach; in a real-world scenario, 
        // you would want to implement a custom repository method for better performance
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), upcomingReservations.size());

        if (start > upcomingReservations.size()) {
            return Page.empty(pageable);
        }

        List<Reservation> pageContent = upcomingReservations.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, upcomingReservations.size());
    }

    @Override
    public List<Reservation> getPastReservations(Long userId, Long currentUserId) {
        // Authorization check: users can only view their own reservations
        if (!userId.equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only view your own reservations");
        }

        // Get all reservations for the user
        List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

        // Filter for past reservations (end date is in the past)
        Instant now = Instant.now();
        return userReservations.stream()
            .filter(r -> r.getEndDate().isBefore(now))
            .toList();
    }

    @Override
    public Page<Reservation> getPastReservationsPaged(Long userId, Long currentUserId, Pageable pageable) {
        // Authorization check: users can only view their own reservations
        if (!userId.equals(currentUserId)) {
            throw new AuthorizationException("Unauthorized: You can only view your own reservations");
        }

        // Get all reservations for the user and filter in memory
        // Note: This is not optimal for large datasets as it loads all data first
        // A better approach would be to add a custom query method in the repository
        List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

        // Filter for past reservations (end date is in the past)
        Instant now = Instant.now();
        List<Reservation> pastReservations = userReservations.stream()
            .filter(r -> r.getEndDate().isBefore(now))
            .toList();

        // Get the paginated result from the filtered list
        // This is a simplified approach; in a real-world scenario, 
        // you would want to implement a custom repository method for better performance
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pastReservations.size());

        if (start > pastReservations.size()) {
            return Page.empty(pageable);
        }

        List<Reservation> pageContent = pastReservations.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, pastReservations.size());
    }

    // Payment methods implementation

    @Override
    public Page<Payment> getAllPaymentsPaged(Pageable pageable) {
        return paymentRepo.findAll(pageable);
    }

    @Override
    public Page<Payment> getPaymentsByUserIdPaged(Long userId, Pageable pageable) {
        return paymentRepo.findByUserId(userId, pageable);
    }

    @Override
    public Page<Payment> getPaymentsByReservationPaged(Reservation reservation, Pageable pageable) {
        return paymentRepo.findByReservation(reservation, pageable);
    }

    // Authentication and validation

    @Override
    public boolean isAuthenticated(Long userId) {
        // In a real application, this would check if the user is logged in
        // For this implementation, we'll just check if the user exists
        return userRepo.existsById(userId);
    }

    @Override
    public boolean isEmailVerified(Long userId) {
        Optional<User> user = userRepo.findById(userId);
        return user.map(User::getEmailVerified).orElse(false);
    }

    // Verification
    @Override
    public boolean verifyEmail(Long userId, String verificationCode) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        // Check if the provided code matches the stored code
        if (user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(verificationCode)) {
            return false;
        }

        // Mark email as verified and clear the verification code
        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        userRepo.save(user);

        return true;
    }

    // Phone verification method removed as per requirements

    // Verification code generation
    @Override
    public String generateEmailVerificationCode(Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        String code = generateVerificationCode();
        user.setEmailVerificationCode(code);
        userRepo.save(user);

        // Send the verification code via email
        try {
            emailService.sendVerificationCode(user.getEmail(), code);
        } catch (Exception e) {
            // Log the error but don't fail the method
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        return code;
    }

    // Token management
    @Override
    public String generateToken(Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Generate a random token
        String token = UUID.randomUUID().toString();

        // Set token expiry to 24 hours from now
        Instant expiry = Instant.now().plus(24, ChronoUnit.HOURS);

        // Save token to user
        user.setAuthToken(token);
        user.setTokenExpiry(expiry);
        userRepo.save(user);

        return token;
    }

    @Override
    public Long validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        // Find user by token
        Optional<User> user = userRepo.findByAuthToken(token);
        if (user.isEmpty()) {
            return null;
        }

        // Check if token is expired
        if (user.get().getTokenExpiry().isBefore(Instant.now())) {
            // Token is expired, invalidate it
            invalidateToken(user.get().getId());
            return null;
        }

        return user.get().getId();
    }

    @Override
    public void invalidateToken(Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setAuthToken(null);
            user.setTokenExpiry(null);
            userRepo.save(user);
        }
    }

    // Rate limiting

    @Override
    public boolean checkReservationRateLimit(Long userId) {
        Instant now = Instant.now();

        // Get or create the list of attempts for this user
        List<Instant> attempts = reservationAttempts.computeIfAbsent(userId, _ -> new ArrayList<>());

        // Remove attempts older than the rate limit window
        Instant cutoff = now.minus(RATE_LIMIT_WINDOW_MINUTES, ChronoUnit.MINUTES);
        attempts.removeIf(timestamp -> timestamp.isBefore(cutoff));

        // Check if the user has exceeded the rate limit
        if (attempts.size() >= MAX_RESERVATION_ATTEMPTS) {
            return false;
        }

        // Add the current attempt
        attempts.add(now);
        return true;
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Payment methods implementation

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepo.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepo.findByUserId(userId);
    }

    @Override
    public List<Payment> getPaymentsByReservation(Reservation reservation) {
        return paymentRepo.findByReservation(reservation);
    }

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    @Override
    public Payment createReservationPayment(Reservation reservation, BigDecimal amount) {
        // Default to CASH payment method if not specified
        return createReservationPayment(reservation, amount, PaymentMethodType.CASH);
    }

    @Override
    public Payment createReservationPayment(Reservation reservation, BigDecimal amount, PaymentMethodType paymentMethod) {
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setUser(reservation.getUser());
        payment.setAmount(amount);
        payment.setPaymentDate(Instant.now());
        payment.setStatus("pending");
        payment.setPaymentMethod(paymentMethod);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        return paymentRepo.save(payment);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        if (!paymentRepo.existsById(id)) {
            throw new ResourceNotFoundException("Payment", "id", id);
        }
        payment.setId(id);
        return paymentRepo.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepo.existsById(id)) {
            throw new ResourceNotFoundException("Payment", "id", id);
        }
        paymentRepo.deleteById(id);
    }
}
