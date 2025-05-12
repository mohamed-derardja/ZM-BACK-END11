package com.zm.zmbackend.controllers;

import com.zm.zmbackend.entities.Reservation;
import com.zm.zmbackend.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<Reservation>> getReservationsByCarId(@PathVariable Long carId) {
        List<Reservation> reservations = reservationService.getReservationsByCarId(carId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Reservation>> getReservationsByDriverId(@PathVariable Long driverId) {
        List<Reservation> reservations = reservationService.getReservationsByDriverId(driverId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable String status) {
        List<Reservation> reservations = reservationService.getReservationsByStatus(status);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationService.createReservation(reservation);
        return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(id, reservation);
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(@PathVariable Long id, @RequestBody String status) {
        try {
            Reservation updatedReservation = reservationService.updateReservationStatus(id, status);
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}