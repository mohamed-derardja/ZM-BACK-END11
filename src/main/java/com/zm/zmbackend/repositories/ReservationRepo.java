package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    List<Reservation> findByCarId(Long carId);
    Page<Reservation> findByCarId(Long carId, Pageable pageable);

    List<Reservation> findByDriverId(Long driverId);
    Page<Reservation> findByDriverId(Long driverId, Pageable pageable);

    List<Reservation> findByStatus(String status);
    Page<Reservation> findByStatus(String status, Pageable pageable);

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
