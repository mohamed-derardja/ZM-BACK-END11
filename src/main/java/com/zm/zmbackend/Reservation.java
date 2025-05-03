package com.zm.zmbackend;

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
    @Column(name = "Reservation_ID", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "Start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "End_date", nullable = false)
    private Instant endDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Car_ID", nullable = false)
    private Car car;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Driver_ID", nullable = false)
    private Driver driver;

    @NotNull
    @Column(name = "Self_drive", nullable = false)
    private Boolean selfDrive = false;

    @NotNull
    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "Status", nullable = false)
    private String status;

    @NotNull
    @Column(name = "Fee", nullable = false, precision = 8, scale = 2)
    private BigDecimal fee;

    @NotNull
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "Updated_at", nullable = false)
    private Instant updatedAt;

}