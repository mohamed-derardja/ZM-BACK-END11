package com.zm.zmbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reservation_ID", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "Start_Date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "End_Date", nullable = false)
    private Instant endDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Car_ID", nullable = false)
    private Car car;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Driver_ID")
    private Driver driver;

    @NotNull
    @Column(name = "Self_drive", nullable = false)
    private Boolean selfDrive = false;

    @NotNull
    @ColumnDefault("'Pending'")
    @Column(name = "Status", nullable = false)
    private String status;

    @NotNull
    @Column(name = "Fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(name = "Cancellation_Fee", precision = 10, scale = 2)
    private BigDecimal cancellationFee;

    @NotNull
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "Updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}