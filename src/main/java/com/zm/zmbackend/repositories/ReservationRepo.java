package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByCarId(Long carId);
    List<Reservation> findByDriverId(Long driverId);
    List<Reservation> findByStatus(String status);

    @Query("SELECT r FROM Reservation r WHERE r.car.id = :carId AND r.status != 'Rejected' AND r.status != 'Ended' " +
           "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservationsForCar(@Param("carId") Long carId, 
                                                       @Param("startDate") Instant startDate, 
                                                       @Param("endDate") Instant endDate);

    @Query("SELECT r FROM Reservation r WHERE r.driver.id = :driverId AND r.status != 'Rejected' AND r.status != 'Ended' " +
           "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findOverlappingReservationsForDriver(@Param("driverId") Long driverId, 
                                                          @Param("startDate") Instant startDate, 
                                                          @Param("endDate") Instant endDate);
}
