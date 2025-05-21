package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DriverService {
    List<Driver> getAllDrivers();
    Page<Driver> getAllDriversPaged(Pageable pageable);
    Optional<Driver> getDriverById(Long id);

    // Business logic methods
    boolean isDriverAvailable(Long driverId, Instant startDate, Instant endDate);

    // Method to check if a driver is generally available (without date constraints)
    boolean isDriverAvailable(Long driverId);

    // Method to update driver availability (for internal use by ReservationService)
    void updateDriverAvailability(Long driverId, Boolean availability);
}
