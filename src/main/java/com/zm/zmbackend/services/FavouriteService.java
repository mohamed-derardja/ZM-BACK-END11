package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Favourite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FavouriteService {
    List<Favourite> getAllFavourites();
    Page<Favourite> getAllFavouritesPaged(Pageable pageable);

    Optional<Favourite> getFavouriteById(Long userId, Long carId);
    Favourite createFavourite(Favourite favourite);
    Favourite updateFavourite(Long userId, Long carId, Favourite favourite);
    void deleteFavourite(Long userId, Long carId);
}
