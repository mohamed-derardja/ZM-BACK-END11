package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.entities.Driver;
import com.zm.zmbackend.entities.PaymentMethodType;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.repositories.ReservationRepo;
import com.zm.zmbackend.services.CarService;
import com.zm.zmbackend.services.DriverService;
import com.zm.zmbackend.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepo reservationRepo;
    private final CarService carService;
    private final DriverService driverService;
    private final PaymentServiceImpl paymentService;
    @Autowired
    public ReservationServiceImpl(ReservationRepo reservationRepo, CarService carService, DriverService driverService, PaymentServiceImpl paymentService) {
        this.reservationRepo = reservationRepo;
        this.carService = carService;
        this.driverService = driverService;

        this.paymentService = paymentService;
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
        // Driver must not be set by user
        if (reservation.getDriver() != null) {
            throw new RuntimeException("Driver cannot be selected during reservation creation. It will be assigned by the admin.");
        }

        if (!validateReservationDates(reservation.getStartDate(), reservation.getEndDate())) {
            throw new RuntimeException("Invalid reservation dates");
        }

        if (!checkCarAvailability(reservation.getCar().getId(), reservation.getStartDate(), reservation.getEndDate())) {
            throw new RuntimeException("Car is not available for the selected dates");
        }

        // Calculate fee without driver
        BigDecimal fee = calculateReservationFee(
                reservation.getCar().getId(),
                null,
                reservation.getStartDate(),
                reservation.getEndDate(),
                true
        );
        reservation.setFee(fee);

        if (reservation.getStatus() == null) {
            reservation.setStatus("Pending");
        }

        carService.updateCarStatus(reservation.getCar().getId(), "Reserved");

        Reservation savedReservation = reservationRepo.save(reservation);

        // Create a payment record for the reservation fee
        PaymentMethodType paymentMethod = savedReservation.getPaymentMethod() != null ? 
                                         savedReservation.getPaymentMethod() : PaymentMethodType.CASH;
        paymentService.createReservationPayment(savedReservation, fee, paymentMethod);

        return savedReservation;
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservation) {
        Optional<Reservation> existingReservationOpt = reservationRepo.findById(id);
        if (existingReservationOpt.isEmpty()) {
            throw new RuntimeException("Reservation not found with id: " + id);
        }

        Reservation existingReservation = existingReservationOpt.get();

        boolean datesChanged = !existingReservation.getStartDate().equals(reservation.getStartDate()) ||
                !existingReservation.getEndDate().equals(reservation.getEndDate());

        boolean carChanged = !existingReservation.getCar().getId().equals(reservation.getCar().getId());

        boolean driverRemoved = existingReservation.getDriver() != null && reservation.getSelfDrive();
        boolean driverChanged = !reservation.getSelfDrive() && (
                existingReservation.getDriver() == null ||
                        (reservation.getDriver() != null &&
                                !reservation.getDriver().getId().equals(existingReservation.getDriver().getId()))
        );

        if (driverChanged) {
            throw new RuntimeException("Driver cannot be changed. You may only remove the driver (switch to self-drive).");
        }

        BigDecimal fee = existingReservation.getFee();
        boolean feeChanged = false;

        if (datesChanged || carChanged || driverRemoved) {
            if (!validateReservationDates(reservation.getStartDate(), reservation.getEndDate())) {
                throw new RuntimeException("Invalid reservation dates");
            }

            if (carChanged && !checkCarAvailability(reservation.getCar().getId(), reservation.getStartDate(), reservation.getEndDate())) {
                throw new RuntimeException("Car is not available for the selected dates");
            }

            if (!reservation.getSelfDrive() && existingReservation.getDriver() != null && datesChanged) {
                if (!checkDriverAvailability(existingReservation.getDriver().getId(), reservation.getStartDate(), reservation.getEndDate())) {
                    throw new RuntimeException("Driver is not available for the selected dates");
                }
            }

            fee = calculateReservationFee(
                    reservation.getCar().getId(),
                    existingReservation.getDriver() != null ? existingReservation.getDriver().getId() : null,
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getSelfDrive() && existingReservation.getDriver() != null
            );
            reservation.setFee(fee);
            feeChanged = true;

            if (carChanged) {
                carService.updateCarStatus(existingReservation.getCar().getId(), "Available");
                carService.updateCarStatus(reservation.getCar().getId(), "Reserved");
            }

            if (driverRemoved) {
                driverService.updateDriverAvailability(existingReservation.getDriver().getId(), true);
            }
        }

        reservation.setId(id);
        reservation.setDriver(existingReservation.getDriver()); // Preserve original driver
        Reservation updatedReservation = reservationRepo.save(reservation);

        // Update payment if fee has changed
        if (feeChanged) {
            PaymentMethodType paymentMethod = updatedReservation.getPaymentMethod() != null ? 
                                             updatedReservation.getPaymentMethod() : PaymentMethodType.CASH;
            paymentService.createReservationPayment(updatedReservation, fee, paymentMethod);
        }

        return updatedReservation;
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
        reservation.setStatus(status);

        if ("Approved".equals(status)) {
            carService.updateCarStatus(reservation.getCar().getId(), "Reserved");

            if (!reservation.getSelfDrive() && reservation.getDriver() != null) {
                driverService.updateDriverAvailability(reservation.getDriver().getId(), false);
            }

            // Create a payment record when the reservation is approved
            PaymentMethodType paymentMethod = reservation.getPaymentMethod() != null ? 
                                             reservation.getPaymentMethod() : PaymentMethodType.CASH;
            paymentService.createReservationPayment(reservation, reservation.getFee(), paymentMethod);
        } else if ("Rejected".equals(status) || "Ended".equals(status)) {
            carService.updateCarStatus(reservation.getCar().getId(), "Available");

            if (!reservation.getSelfDrive() && reservation.getDriver() != null) {
                driverService.updateDriverAvailability(reservation.getDriver().getId(), true);
            }
        }

        return reservationRepo.save(reservation);
    }

    @Override
    public boolean validateReservationDates(Instant startDate, Instant endDate) {
        if (startDate.isBefore(Instant.now())) {
            return false;
        }

        if (endDate.isBefore(startDate)) {
            return false;
        }

        Duration duration = Duration.between(startDate, endDate);
        return duration.toDays() <= 90;
    }

    @Override
    public boolean checkCarAvailability(Long carId, Instant startDate, Instant endDate) {
        return carService.isCarAvailable(carId, startDate, endDate);
    }

    @Override
    public boolean checkDriverAvailability(Long driverId, Instant startDate, Instant endDate) {
        if (driverId == null) {
            return true;
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

        Duration duration = Duration.between(startDate, endDate);
        long days = duration.toDays();
        if (days < 1) {
            days = 1;
        }

        fee = fee.add(car.getRentalPricePerDay().multiply(BigDecimal.valueOf(days)));

        if (driverId != null) {
            Optional<Driver> optionalDriver = driverService.getDriverById(driverId);
            if (optionalDriver.isPresent()) {
                Driver driver = optionalDriver.get();
                BigDecimal driverCost = driver.getDailyWage().multiply(BigDecimal.valueOf(days));

                if (selfDrive) {
                    driverCost = driverCost
                            .divide(BigDecimal.valueOf(2), 0, RoundingMode.CEILING); // 0 = no decimal places
                }

                fee = fee.add(driverCost);
            }
        }

        return fee;
    }
}
