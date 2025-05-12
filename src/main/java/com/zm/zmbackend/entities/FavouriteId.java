package com.zm.zmbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class FavouriteId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1121739471773065286L;
    @NotNull
    @Column(name = "User_ID", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "Car_ID", nullable = false)
    private Long carId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FavouriteId entity = (FavouriteId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.carId, entity.carId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, carId);
    }

}