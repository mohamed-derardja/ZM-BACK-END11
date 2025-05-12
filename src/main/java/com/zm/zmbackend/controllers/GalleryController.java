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

    @PostMapping
    public ResponseEntity<Gallery> createGallery(@RequestBody Gallery gallery) {
        Gallery savedGallery = galleryService.createGallery(gallery);
        return new ResponseEntity<>(savedGallery, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gallery> updateGallery(@PathVariable Long id, @RequestBody Gallery gallery) {
        try {
            Gallery updatedGallery = galleryService.updateGallery(id, gallery);
            return new ResponseEntity<>(updatedGallery, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGallery(@PathVariable Long id) {
        try {
            galleryService.deleteGallery(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
