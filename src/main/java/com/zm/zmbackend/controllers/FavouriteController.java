package com.zm.zmbackend.controllers;

import com.zm.zmbackend.enteties.Favourite;
import com.zm.zmbackend.enteties.FavouriteId;
import com.zm.zmbackend.services.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;

    @Autowired
    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @GetMapping
    public ResponseEntity<List<Favourite>> getAllFavourites() {
        List<Favourite> favourites = favouriteService.getAllFavourites();
        return new ResponseEntity<>(favourites, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{carId}")
    public ResponseEntity<Favourite> getFavouriteById(@PathVariable Long userId, @PathVariable Long carId) {
        Optional<Favourite> favourite = favouriteService.getFavouriteById(userId, carId);
        return favourite.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Favourite> createFavourite(@RequestBody Favourite favourite) {
        Favourite savedFavourite = favouriteService.createFavourite(favourite);
        return new ResponseEntity<>(savedFavourite, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/{carId}")
    public ResponseEntity<Favourite> updateFavourite(
            @PathVariable Long userId, 
            @PathVariable Long carId, 
            @RequestBody Favourite favourite) {
        try {
            Favourite updatedFavourite = favouriteService.updateFavourite(userId, carId, favourite);
            return new ResponseEntity<>(updatedFavourite, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userId}/{carId}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable Long userId, @PathVariable Long carId) {
        try {
            favouriteService.deleteFavourite(userId, carId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
