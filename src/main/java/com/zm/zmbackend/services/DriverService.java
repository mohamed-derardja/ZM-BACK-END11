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
    Driver createDriver(Driver driver);
    Driver updateDriver(Long id, Driver driver);
    void deleteDriver(Long id);

    // Business logic methods
    boolean isDriverAvailable(Long driverId, Instant startDate, Instant endDate);
    void updateDriverAvailability(Long driverId, Boolean availability);
}
