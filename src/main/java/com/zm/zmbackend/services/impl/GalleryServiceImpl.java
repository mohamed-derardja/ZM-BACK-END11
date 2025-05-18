package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.entities.Gallery;
import com.zm.zmbackend.exceptions.ResourceNotFoundException;
import com.zm.zmbackend.repositories.GalleryRepo;
import com.zm.zmbackend.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GalleryServiceImpl implements GalleryService {

    private final GalleryRepo galleryRepo;

    @Autowired
    public GalleryServiceImpl(GalleryRepo galleryRepo) {
        this.galleryRepo = galleryRepo;
    }

    @Override
    public List<Gallery> getAllGalleries() {
        return galleryRepo.findAll();
    }

    @Override
    public Page<Gallery> getAllGalleriesPaged(Pageable pageable) {
        return galleryRepo.findAll(pageable);
    }

    @Override
    public Optional<Gallery> getGalleryById(Long id) {
        return galleryRepo.findById(id);
    }

    @Override
    public List<Gallery> getGalleriesByCarId(Long carId) {
        return galleryRepo.findByCarId(carId);
    }

    @Override
    public Page<Gallery> getGalleriesByCarIdPaged(Long carId, Pageable pageable) {
        return galleryRepo.findByCarId(carId, pageable);
    }

    @Override
    public Gallery createGallery(Gallery gallery) {
        return galleryRepo.save(gallery);
    }

    @Override
    public Gallery updateGallery(Long id, Gallery gallery) {
        if (!galleryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Gallery", "id", id);
        }
        gallery.setId(id);
        return galleryRepo.save(gallery);
    }

    @Override
    public void deleteGallery(Long id) {
        if (!galleryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Gallery", "id", id);
        }
        galleryRepo.deleteById(id);
    }
}
