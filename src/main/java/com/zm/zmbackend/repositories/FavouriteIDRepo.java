package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Favourite;
import com.zm.zmbackend.entities.FavouriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteIDRepo extends JpaRepository<Favourite, FavouriteId> {
}
