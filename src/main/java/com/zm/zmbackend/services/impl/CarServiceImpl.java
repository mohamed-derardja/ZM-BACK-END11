package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.repositories.CarRepo;
import com.zm.zmbackend.repositories.ReservationRepo;
import com.zm.zmbackend.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepo carRepo;
    private final ReservationRepo reservationRepo;

    @Autowired
    public CarServiceImpl(CarRepo carRepo, ReservationRepo reservationRepo) {
        this.carRepo = carRepo;
        this.reservationRepo = reservationRepo;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepo.findAll();
    }

    @Override
    public Page<Car> getAllCarsPaged(Pageable pageable) {
        return carRepo.findAll(pageable);
    }

    @Override
    public Optional<Car> getCarById(Long id) {
        return carRepo.findById(id);
    }

    @Override
    public Car createCar(Car car) {
        return carRepo.save(car);
    }

    @Override
    public Car updateCar(Long id, Car car) {
        if (!carRepo.existsById(id)) {
            throw new RuntimeException("Car not found with id: " + id);
        }
        car.setId(id);
        return carRepo.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepo.existsById(id)) {
            throw new RuntimeException("Car not found with id: " + id);
        }
        carRepo.deleteById(id);
    }

    @Override
    public List<Car> getAvailableCars() {
        return carRepo.findByRentalStatus("Available");
    }

    @Override
    public Page<Car> getAvailableCarsPaged(Pageable pageable) {
        return carRepo.findByRentalStatus("Available", pageable);
    }

    @Override
    public Page<Car> getCarsByBrandPaged(String brand, Pageable pageable) {
        return carRepo.findByBrand(brand, pageable);
    }

    @Override
    public Page<Car> getCarsByModelPaged(String model, Pageable pageable) {
        return carRepo.findByModel(model, pageable);
    }

    @Override
    public Page<Car> getCarsByRatingRangePaged(Long minRating, Long maxRating, Pageable pageable) {
        return carRepo.findByRatingBetween(minRating, maxRating, pageable);
    }

    @Override
    public boolean isCarAvailable(Long carId, Instant startDate, Instant endDate) {
        Optional<Car> optionalCar = carRepo.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new RuntimeException("Car not found with id: " + carId);
        }

        Car car = optionalCar.get();
        if (!"Available".equals(car.getRentalStatus())) {
            return false;
        }

        // Check if there are any overlapping reservations
        List<Reservation> overlappingReservations = reservationRepo.findOverlappingReservationsForCar(carId, startDate, endDate);
        return overlappingReservations.isEmpty();
    }

    @Override
    public void updateCarStatus(Long carId, String status) {
        Optional<Car> optionalCar = carRepo.findById(carId);
        if (optionalCar.isEmpty()) {
            throw new RuntimeException("Car not found with id: " + carId);
        }

        Car car = optionalCar.get();
        car.setRentalStatus(status);
        carRepo.save(car);
    }
}
