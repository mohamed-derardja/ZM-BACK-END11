package com.zm.zmbackend.services;

import com.zm.zmbackend.enteties.Driver;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DriverService {
    List<Driver> getAllDrivers();
    Optional<Driver> getDriverById(Long id);
    Driver createDriver(Driver driver);
    Driver updateDriver(Long id, Driver driver);
    void deleteDriver(Long id);

    // Business logic methods
    List<Driver> getAvailableDrivers();
    boolean isDriverAvailable(Long driverId, Instant startDate, Instant endDate);
    Driver updateDriverAvailability(Long driverId, Boolean availability);
}
