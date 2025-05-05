package com.zm.zmbackend.services;

import com.zm.zmbackend.enteties.Payment;
import com.zm.zmbackend.enteties.Reservation;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);
    List<Payment> getPaymentsByUserId(Long userId);
    List<Payment> getPaymentsByReservation(Reservation reservation);
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}
