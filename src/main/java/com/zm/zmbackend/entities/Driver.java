package com.zm.zmbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "driver")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Driver_ID", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "Picture", nullable = false)
    private String picture;

    @Size(max = 255)
    @NotNull
    @Column(name = "First_name", nullable = false)
    private String firstName;

    @Size(max = 255)
    @NotNull
    @Column(name = "Last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "Birthday", nullable = false)
    private LocalDate birthday;

    @Size(max = 255)
    @NotNull
    @Column(name = "Phone_number", nullable = false)
    private String phoneNumber;

    @NotNull
    @Lob
    @Column(name = "Address", nullable = false)
    private String address;

    @Size(max = 255)
    @NotNull
    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @NotNull
    @ColumnDefault("1428.00")
    @Column(name = "Daily_wage", nullable = false, precision = 8, scale = 2)
    private BigDecimal dailyWage;

    @NotNull
    @ColumnDefault("178.00")
    @Column(name = "Hourly_wage", nullable = false, precision = 8, scale = 2)
    private BigDecimal hourlyWage;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "Availability", nullable = false)
    private Boolean availability = false;

    @NotNull
    @ColumnDefault("'Active'")
    @Lob
    @Column(name = "Status", nullable = false)
    private String status;

    @ColumnDefault("'2'")
    @Column(name = "Years_of_experience", columnDefinition = "int UNSIGNED not null")
    private Long yearsOfExperience;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Car_ID", nullable = false)
    private Car car;

    @Column(name = "Rating", columnDefinition = "int UNSIGNED not null")
    private Long rating;

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