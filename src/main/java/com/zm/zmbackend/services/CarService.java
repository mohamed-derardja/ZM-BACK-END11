package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CarService {
    List<Car> getAllCars();
    Page<Car> getAllCarsPaged(Pageable pageable);
    Optional<Car> getCarById(Long id);

    // Business logic methods
    List<Car> getAvailableCars();
    Page<Car> getAvailableCarsPaged(Pageable pageable);
    boolean isCarAvailable(Long carId, Instant startDate, Instant endDate);
    void updateCarStatus(Long carId, String status);

    // Paginated finder methods
    Page<Car> getCarsByBrandPaged(String brand, Pageable pageable);
    Page<Car> getCarsByModelPaged(String model, Pageable pageable);
    Page<Car> getCarsByRatingRangePaged(Long minRating, Long maxRating, Pageable pageable);
}
