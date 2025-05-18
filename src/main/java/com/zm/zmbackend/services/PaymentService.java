package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.PaymentMethodType;
import com.zm.zmbackend.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> getAllPayments();
    Page<Payment> getAllPaymentsPaged(Pageable pageable);

    Optional<Payment> getPaymentById(Long id);

    List<Payment> getPaymentsByUserId(Long userId);
    Page<Payment> getPaymentsByUserIdPaged(Long userId, Pageable pageable);

    List<Payment> getPaymentsByReservation(Reservation reservation);
    Page<Payment> getPaymentsByReservationPaged(Reservation reservation, Pageable pageable);

    Payment createPayment(Payment payment);
    Payment createReservationPayment(Reservation reservation, BigDecimal amount);
    Payment createReservationPayment(Reservation reservation, BigDecimal amount, PaymentMethodType paymentMethod);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}
