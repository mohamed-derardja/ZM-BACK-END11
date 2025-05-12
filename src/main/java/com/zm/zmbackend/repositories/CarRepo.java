package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepo extends JpaRepository<Car, Long> {
    List<Car> findByRentalStatus(String rentalStatus);
    List<Car> findByBrand(String brand);
    List<Car> findByModel(String model);
    List<Car> findByRatingBetween(Long minRating, Long maxRating);
}
