package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    /**
     * Find all payments for a specific user
     * @param userId the ID of the user
     * @return list of payments for the user
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Find all payments for a specific user with pagination
     * @param userId the ID of the user
     * @param pageable pagination information
     * @return page of payments for the user
     */
    Page<Payment> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all payments for a specific reservation
     * @param reservation the reservation entity
     * @return list of payments for the reservation
     */
    List<Payment> findByReservation(Reservation reservation);

    /**
     * Find all payments for a specific reservation with pagination
     * @param reservation the reservation entity
     * @param pageable pagination information
     * @return page of payments for the reservation
     */
    Page<Payment> findByReservation(Reservation reservation, Pageable pageable);
}
