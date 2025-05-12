package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.entities.Driver;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.repositories.ReservationRepo;
import com.zm.zmbackend.services.CarService;
import com.zm.zmbackend.services.DriverService;
import com.zm.zmbackend.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepo reservationRepo;
    private final CarService carService;
    private final DriverService driverService;

    @Autowired
    public ReservationServiceImpl(ReservationRepo reservationRepo, CarService carService, DriverService driverService) {
        this.reservationRepo = reservationRepo;
        this.carService = carService;
        this.driverService = driverService;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepo.findAll();
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepo.findById(id);
    }

    @Override
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepo.findByUserId(userId);
    }

    @Override
    public List<Reservation> getReservationsByCarId(Long carId) {
        return reservationRepo.findByCarId(carId);
    }

    @Override
    public List<Reservation> getReservationsByDriverId(Long driverId) {
        return reservationRepo.findByDriverId(driverId);
    }

    @Override
    public List<Reservation> getReservationsByStatus(String status) {
        return reservationRepo.findByStatus(status);
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        // Validate reservation dates
        if (!validateReservationDates(reservation.getStartDate(), reservation.getEndDate())) {
            throw new RuntimeException("Invalid reservation dates");
        }

        // Check car availability
        if (!checkCarAvailability(reservation.getCar().getId(), reservation.getStartDate(), reservation.getEndDate())) {
            throw new RuntimeException("Car is not available for the selected dates");
        }

        // Check driver availability if not self-drive
        if (!reservation.getSelfDrive() && reservation.getDriver() != null) {
            if (!checkDriverAvailability(reservation.getDriver().getId(), reservation.getStartDate(), reservation.getEndDate())) {
                throw new RuntimeException("Driver is not available for the selected dates");
            }
        }

        // Calculate fee
        BigDecimal fee = calculateReservationFee(
            reservation.getCar().getId(),
            reservation.getDriver() != null ? reservation.getDriver().getId() : null,
            reservation.getStartDate(),
            reservation.getEndDate(),
            reservation.getSelfDrive()
        );
        reservation.setFee(fee);

        // Set initial status if not set
        if (reservation.getStatus() == null) {
            reservation.setStatus("Pending");
        }

        // Update car status
        carService.updateCarStatus(reservation.getCar().getId(), "Reserved");

        // Update driver availability if not self-drive
        if (!reservation.getSelfDrive() && reservation.getDriver() != null) {
            driverService.updateDriverAvailability(reservation.getDriver().getId(), false);
        }

        return reservationRepo.save(reservation);
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservation) {
        Optional<Reservation> existingReservationOpt = reservationRepo.findById(id);
        if (existingReservationOpt.isEmpty()) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        Reservation existingReservation = existingReservationOpt.get();

        // Check if dates have changed
        boolean datesChanged = !existingReservation.getStartDate().equals(reservation.getStartDate()) ||
                              !existingReservation.getEndDate().equals(reservation.getEndDate());

        // Check if car has changed
        boolean carChanged = !existingReservation.getCar().getId().equals(reservation.getCar().getId());

        // Check if driver has changed
        boolean driverChanged = (existingReservation.getDriver() == null && reservation.getDriver() != null) ||
                               (existingReservation.getDriver() != null && reservation.getDriver() == null) ||
                               (existingReservation.getDriver() != null && reservation.getDriver() != null && 
                                !existingReservation.getDriver().getId().equals(reservation.getDriver().getId()));

        // If dates, car, or driver has changed, validate availability
        if (datesChanged || carChanged || driverChanged) {
            // Validate reservation dates
            if (!validateReservationDates(reservation.getStartDate(), reservation.getEndDate())) {
                throw new RuntimeException("Invalid reservation dates");
            }

            // Check car availability
            if (carChanged && !checkCarAvailability(reservation.getCar().getId(), reservation.getStartDate(), reservation.getEndDate())) {
                throw new RuntimeException("Car is not available for the selected dates");
            }

            // Check driver availability if not self-drive
            if (!reservation.getSelfDrive() && reservation.getDriver() != null && 
                (driverChanged || datesChanged) && 
                !checkDriverAvailability(reservation.getDriver().getId(), reservation.getStartDate(), reservation.getEndDate())) {
                throw new RuntimeException("Driver is not available for the selected dates");
            }

            // Recalculate fee
            BigDecimal fee = calculateReservationFee(
                reservation.getCar().getId(),
                reservation.getDriver() != null ? reservation.getDriver().getId() : null,
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getSelfDrive()
            );
            reservation.setFee(fee);

            // Update old car status if car has changed
            if (carChanged) {
                carService.updateCarStatus(existingReservation.getCar().getId(), "Available");
                carService.updateCarStatus(reservation.getCar().getId(), "Reserved");
            }

            // Update old driver availability if driver has changed
            if (driverChanged) {
                if (existingReservation.getDriver() != null) {
                    driverService.updateDriverAvailability(existingReservation.getDriver().getId(), true);
                }
                if (reservation.getDriver() != null && !reservation.getSelfDrive()) {
                    driverService.updateDriverAvailability(reservation.getDriver().getId(), false);
                }
            }
        }

        reservation.setId(id);
        return reservationRepo.save(reservation);
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepo.existsById(id)) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }
        reservationRepo.deleteById(id);
    }

    @Override
    public Reservation updateReservationStatus(Long id, String status) {
        Optional<Reservation> optionalReservation = reservationRepo.findById(id);
        if (optionalReservation.isEmpty()) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        Reservation reservation = optionalReservation.get();
        String oldStatus = reservation.getStatus();
        reservation.setStatus(status);

        // Update car and driver status based on reservation status
        if ("Approved".equals(status)) {
            // When a reservation is approved, mark the car as reserved
            carService.updateCarStatus(reservation.getCar().getId(), "Reserved");

            // If a driver is assigned and it's not self-drive, mark the driver as unavailable
            if (!reservation.getSelfDrive() && reservation.getDriver() != null) {
                driverService.updateDriverAvailability(reservation.getDriver().getId(), false);
            }
        } else if ("Rejected".equals(status) || "Ended".equals(status)) {
            // When a reservation is rejected or ended, mark the car as available
            carService.updateCarStatus(reservation.getCar().getId(), "Available");

            // If a driver was assigned and it's not self-drive, mark the driver as available
            if (!reservation.getSelfDrive() && reservation.getDriver() != null) {
                driverService.updateDriverAvailability(reservation.getDriver().getId(), true);
            }
        }

        return reservationRepo.save(reservation);
    }

    @Override
    public boolean validateReservationDates(Instant startDate, Instant endDate) {
        // Check if start date is in the future
        if (startDate.isBefore(Instant.now())) {
            return false;
        }

        // Check if end date is after start date
        if (endDate.isBefore(startDate)) {
            return false;
        }

        // Check if the duration is reasonable (e.g., not more than 30 days)
        Duration duration = Duration.between(startDate, endDate);
        return duration.toDays() <= 30;
    }

    @Override
    public boolean checkCarAvailability(Long carId, Instant startDate, Instant endDate) {
        return carService.isCarAvailable(carId, startDate, endDate);
    }

    @Override
    public boolean checkDriverAvailability(Long driverId, Instant startDate, Instant endDate) {
        if (driverId == null) {
            return true; // No driver requested, so availability check passes
        }
        return driverService.isDriverAvailable(driverId, startDate, endDate);
    }

    @Override
    public BigDecimal calculateReservationFee(Long carId, Long driverId, Instant startDate, Instant endDate, Boolean selfDrive) {
        Optional<Car> optionalCar = carService.getCarById(carId);
        if (optionalCar.isEmpty()) {
            throw new RuntimeException("Car not found with id: " + carId);
        }

        Car car = optionalCar.get();
        BigDecimal fee = BigDecimal.ZERO;

        // Calculate duration in days
        Duration duration = Duration.between(startDate, endDate);
        long days = duration.toDays();
        if (days < 1) {
            days = 1; // Minimum 1 day
        }

        // Add car rental fee
        fee = fee.add(car.getRentalPricePerDay().multiply(BigDecimal.valueOf(days)));

        // Add driver fee if not self-drive
        if (!selfDrive && driverId != null) {
            Optional<Driver> optionalDriver = driverService.getDriverById(driverId);
            if (optionalDriver.isPresent()) {
                Driver driver = optionalDriver.get();
                fee = fee.add(driver.getDailyWage().multiply(BigDecimal.valueOf(days)));
            }
        }

        return fee;
    }
}
