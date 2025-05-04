package com.zm.zmbackend.enteties;

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
@Table(name = "payments")
public class Payment {
    @Id
    @Column(name = "Payment_ID", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "Reservation_ID", nullable = false)
    private Long reservationId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 8, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "Payment_date", nullable = false)
    private Instant paymentDate;

    @NotNull
    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "Status", nullable = false)
    private String status;

    @NotNull
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "Updated_at", nullable = false)
    private Instant updatedAt;

}