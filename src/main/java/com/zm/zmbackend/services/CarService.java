package com.zm.zmbackend.services;

import com.zm.zmbackend.enteties.Car;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CarService {
    List<Car> getAllCars();
    Optional<Car> getCarById(Long id);
    Car createCar(Car car);
    Car updateCar(Long id, Car car);
    void deleteCar(Long id);

    // Business logic methods
    List<Car> getAvailableCars();
    boolean isCarAvailable(Long carId, Instant startDate, Instant endDate);
    Car updateCarStatus(Long carId, String status);
}
