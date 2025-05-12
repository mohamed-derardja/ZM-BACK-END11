package com.zm.zmbackend.controllers;

import com.zm.zmbackend.entities.Payment;
import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.services.PaymentService;
import com.zm.zmbackend.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;

    @Autowired
    public PaymentController(PaymentService paymentService, ReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable Long userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<Payment>> getPaymentsByReservationId(@PathVariable Long reservationId) {
        Optional<Reservation> reservation = reservationService.getReservationById(reservationId);
        if (reservation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Payment> payments = paymentService.getPaymentsByReservation(reservation.get());
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.createPayment(payment);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        try {
            Payment updatedPayment = paymentService.updatePayment(id, payment);
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
