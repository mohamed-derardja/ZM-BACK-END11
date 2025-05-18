package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepo extends JpaRepository<Car, Long> {
    List<Car> findByRentalStatus(String rentalStatus);
    Page<Car> findByRentalStatus(String rentalStatus, Pageable pageable);

    List<Car> findByBrand(String brand);
    Page<Car> findByBrand(String brand, Pageable pageable);

    List<Car> findByModel(String model);
    Page<Car> findByModel(String model, Pageable pageable);

    List<Car> findByRatingBetween(Long minRating, Long maxRating);
    Page<Car> findByRatingBetween(Long minRating, Long maxRating, Pageable pageable);
}
