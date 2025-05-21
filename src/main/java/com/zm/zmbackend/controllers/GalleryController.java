package com.zm.zmbackend.controllers;

import com.zm.zmbackend.entities.Gallery;
import com.zm.zmbackend.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/galleries")
public class GalleryController {

    private final GalleryService galleryService;

    @Autowired
    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping
    public ResponseEntity<List<Gallery>> getAllGalleries() {
        List<Gallery> galleries = galleryService.getAllGalleries();
        return new ResponseEntity<>(galleries, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gallery> getGalleryById(@PathVariable Long id) {
        Optional<Gallery> gallery = galleryService.getGalleryById(id);
        return gallery.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<List<Gallery>> getGalleriesByCarId(@PathVariable Long carId) {
        List<Gallery> galleries = galleryService.getGalleriesByCarId(carId);
        return new ResponseEntity<>(galleries, HttpStatus.OK);
    }

}
