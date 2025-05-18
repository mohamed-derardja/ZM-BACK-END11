package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.PaymentMethodType;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.exceptions.ResourceNotFoundException;
import com.zm.zmbackend.repositories.PaymentRepo;
import com.zm.zmbackend.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;

    @Autowired
    public PaymentServiceImpl(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    @Override
    public Page<Payment> getAllPaymentsPaged(Pageable pageable) {
        return paymentRepo.findAll(pageable);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepo.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepo.findByUserId(userId);
    }

    @Override
    public Page<Payment> getPaymentsByUserIdPaged(Long userId, Pageable pageable) {
        return paymentRepo.findByUserId(userId, pageable);
    }

    @Override
    public List<Payment> getPaymentsByReservation(Reservation reservation) {
        return paymentRepo.findByReservation(reservation);
    }

    @Override
    public Page<Payment> getPaymentsByReservationPaged(Reservation reservation, Pageable pageable) {
        return paymentRepo.findByReservation(reservation, pageable);
    }

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    @Override
    public Payment createReservationPayment(Reservation reservation, BigDecimal amount) {
        // Default to CASH payment method if not specified
        return createReservationPayment(reservation, amount, PaymentMethodType.CASH);
    }

    @Override
    public Payment createReservationPayment(Reservation reservation, BigDecimal amount, PaymentMethodType paymentMethod) {
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setUser(reservation.getUser());
        payment.setAmount(amount);
        payment.setPaymentDate(Instant.now());
        payment.setStatus("pending");
        payment.setPaymentMethod(paymentMethod);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        return paymentRepo.save(payment);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        if (!paymentRepo.existsById(id)) {
            throw new ResourceNotFoundException("Payment", "id", id);
        }
        payment.setId(id);
        return paymentRepo.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepo.existsById(id)) {
            throw new ResourceNotFoundException("Payment", "id", id);
        }
        paymentRepo.deleteById(id);
    }
}
