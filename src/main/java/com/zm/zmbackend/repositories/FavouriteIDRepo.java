package com.zm.zmbackend.repositories;

import com.zm.zmbackend.enteties.Favourite;
import com.zm.zmbackend.enteties.FavouriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteIDRepo extends JpaRepository<Favourite, FavouriteId> {
}
