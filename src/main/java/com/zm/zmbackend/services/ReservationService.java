package com.zm.zmbackend.services;

import com.zm.zmbackend.enteties.Reservation;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<Reservation> getAllReservations();
    Optional<Reservation> getReservationById(Long id);
    List<Reservation> getReservationsByUserId(Long userId);
    List<Reservation> getReservationsByCarId(Long carId);
    List<Reservation> getReservationsByDriverId(Long driverId);
    List<Reservation> getReservationsByStatus(String status);
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
