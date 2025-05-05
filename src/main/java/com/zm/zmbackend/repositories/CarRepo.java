package com.zm.zmbackend.repositories;

import com.zm.zmbackend.enteties.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepo extends JpaRepository<Car, Long> {
    List<Car> findByRentalStatus(String rentalStatus);
}
