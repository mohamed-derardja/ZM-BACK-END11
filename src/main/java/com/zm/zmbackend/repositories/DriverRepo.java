package com.zm.zmbackend.repositories;

import com.zm.zmbackend.enteties.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Long> {
    List<Driver> findByAvailabilityAndStatus(Boolean availability, String status);
}
