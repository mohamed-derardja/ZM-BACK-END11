package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Favourite;
import com.zm.zmbackend.entities.FavouriteId;
import com.zm.zmbackend.repositories.FavouriteRepo;
import com.zm.zmbackend.services.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteRepo favouriteRepo;

    @Autowired
    public FavouriteServiceImpl(FavouriteRepo favouriteRepo) {
        this.favouriteRepo = favouriteRepo;
    }

    @Override
    public List<Favourite> getAllFavourites() {
        return favouriteRepo.findAll();
    }

    @Override
    public Optional<Favourite> getFavouriteById(Long userId, Long carId) {
        FavouriteId id = createFavouriteId(userId, carId);
        return favouriteRepo.findById(id);
    }

    @Override
    public Favourite createFavourite(Favourite favourite) {
        return favouriteRepo.save(favourite);
    }

    @Override
    public Favourite updateFavourite(Long userId, Long carId, Favourite favourite) {
        FavouriteId id = createFavouriteId(userId, carId);
        
        if (!favouriteRepo.existsById(id)) {
            throw new RuntimeException("Favourite not found with userId: " + userId + " and carId: " + carId);
        }
        
        favourite.setId(id);
        return favouriteRepo.save(favourite);
    }

    @Override
    public void deleteFavourite(Long userId, Long carId) {
        FavouriteId id = createFavouriteId(userId, carId);
        
        if (!favouriteRepo.existsById(id)) {
            throw new RuntimeException("Favourite not found with userId: " + userId + " and carId: " + carId);
        }
        
        favouriteRepo.deleteById(id);
    }
    
    private FavouriteId createFavouriteId(Long userId, Long carId) {
        FavouriteId id = new FavouriteId();
        id.setUserId(userId);
        id.setCarId(carId);
        return id;
    }
}