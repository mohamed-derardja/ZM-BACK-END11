package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<Reservation> getAllReservations();
    Page<Reservation> getAllReservationsPaged(Pageable pageable);

    Optional<Reservation> getReservationById(Long id);

    List<Reservation> getReservationsByUserId(Long userId);
    Page<Reservation> getReservationsByUserIdPaged(Long userId, Pageable pageable);

    List<Reservation> getReservationsByCarId(Long carId);
    Page<Reservation> getReservationsByCarIdPaged(Long carId, Pageable pageable);

    List<Reservation> getReservationsByDriverId(Long driverId);
    Page<Reservation> getReservationsByDriverIdPaged(Long driverId, Pageable pageable);

    List<Reservation> getReservationsByStatus(String status);
    Page<Reservation> getReservationsByStatusPaged(String status, Pageable pageable);

    Reservation createReservation(Reservation reservation);
    Reservation updateReservation(Long id, Reservation reservation);
    void deleteReservation(Long id);
    Reservation updateReservationStatus(Long id, String status);

    // Business logic methods
    boolean validateReservationDates(Instant startDate, Instant endDate);
    boolean checkCarAvailability(Long carId, Instant startDate, Instant endDate);
    boolean checkDriverAvailability(Long driverId, Instant startDate, Instant endDate);
    BigDecimal calculateReservationFee(Long carId, Long driverId, Instant startDate, Instant endDate, Boolean selfDrive);
}
