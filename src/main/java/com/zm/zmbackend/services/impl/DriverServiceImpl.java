package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Driver;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.exceptions.ResourceNotFoundException;
import com.zm.zmbackend.repositories.DriverRepo;
import com.zm.zmbackend.repositories.ReservationRepo;
import com.zm.zmbackend.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepo driverRepo;
    private final ReservationRepo reservationRepo;

    @Autowired
    public DriverServiceImpl(DriverRepo driverRepo, ReservationRepo reservationRepo) {
        this.driverRepo = driverRepo;
        this.reservationRepo = reservationRepo;
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepo.findAll();
    }

    @Override
    public Page<Driver> getAllDriversPaged(Pageable pageable) {
        return driverRepo.findAll(pageable);
    }

    @Override
    public Optional<Driver> getDriverById(Long id) {
        return driverRepo.findById(id);
    }

    @Override
    public boolean isDriverAvailable(Long driverId, Instant startDate, Instant endDate) {
        Optional<Driver> optionalDriver = driverRepo.findById(driverId);
        if (optionalDriver.isEmpty()) {
            throw new ResourceNotFoundException("Driver", "id", driverId);
        }

        Driver driver = optionalDriver.get();
        if (!driver.getAvailability() || !"Active".equals(driver.getStatus())) {
            return false;
        }

        // If no dates are provided, just check the driver's general availability
        if (startDate == null || endDate == null) {
            return driver.getAvailability() && "Active".equals(driver.getStatus());
        }

        // Check if there are any overlapping reservations
        List<Reservation> overlappingReservations = reservationRepo.findOverlappingReservationsForDriver(driverId, startDate, endDate);
        return overlappingReservations.isEmpty();
    }

    @Override
    public boolean isDriverAvailable(Long driverId) {
        Optional<Driver> optionalDriver = driverRepo.findById(driverId);
        if (optionalDriver.isEmpty()) {
            throw new ResourceNotFoundException("Driver", "id", driverId);
        }

        Driver driver = optionalDriver.get();
        return driver.getAvailability() && "Active".equals(driver.getStatus());
    }

    @Override
    public void updateDriverAvailability(Long driverId, Boolean availability) {
        Optional<Driver> optionalDriver = driverRepo.findById(driverId);
        if (optionalDriver.isEmpty()) {
            throw new ResourceNotFoundException("Driver", "id", driverId);
        }

        Driver driver = optionalDriver.get();
        driver.setAvailability(availability);
        driverRepo.save(driver);
    }
}
