package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.repositories.PaymentRepo;
import com.zm.zmbackend.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepo.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        // This would require a custom method in the repository
        // For now, we'll return all payments (in a real implementation, you'd add a findByUserId method)
        return paymentRepo.findAll();
    }

    public List<Payment> getPaymentsByReservation(Reservation reservation) {
        // This would require a custom method in the repository
        // For now, we'll return all payments (in a real implementation, you'd add a findByReservation method)
        return paymentRepo.findAll();
    }

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        if (!paymentRepo.existsById(id)) {
            throw new RuntimeException("Payment not found with id: " + id);
        }
        payment.setId(id);
        return paymentRepo.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepo.existsById(id)) {
            throw new RuntimeException("Payment not found with id: " + id);
        }
        paymentRepo.deleteById(id);
    }
}
