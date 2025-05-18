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
    public Driver createDriver(Driver driver) {
        return driverRepo.save(driver);
    }

    @Override
    public Driver updateDriver(Long id, Driver driver) {
        if (!driverRepo.existsById(id)) {
            throw new ResourceNotFoundException("Driver", "id", id);
        }
        driver.setId(id);
        return driverRepo.save(driver);
    }

    @Override
    public void deleteDriver(Long id) {
        if (!driverRepo.existsById(id)) {
            throw new ResourceNotFoundException("Driver", "id", id);
        }
        driverRepo.deleteById(id);
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

        // Check if there are any overlapping reservations
        List<Reservation> overlappingReservations = reservationRepo.findOverlappingReservationsForDriver(driverId, startDate, endDate);
        return overlappingReservations.isEmpty();
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
