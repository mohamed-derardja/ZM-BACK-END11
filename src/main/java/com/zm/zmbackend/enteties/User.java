package com.zm.zmbackend.enteties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "User_ID", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "Picture")
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
    @Column(name = "Email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "Password", nullable = false)
    private String password;



    @NotNull
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "Updated_at", nullable = false)
    private Instant updatedAt;

}