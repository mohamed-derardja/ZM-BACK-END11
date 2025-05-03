package com.zm.zmbackend;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "favourites")
public class Favourite {
    @EmbeddedId
    private FavouriteId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @MapsId("carId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Car_ID", nullable = false)
    private Car car;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "Created_at", nullable = false)
    private Instant createdAt;

}