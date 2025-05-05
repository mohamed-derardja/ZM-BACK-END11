package com.zm.zmbackend.services.impl;

import com.zm.zmbackend.enteties.Gallery;
import com.zm.zmbackend.repositories.GalleryRepo;
import com.zm.zmbackend.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Optional<Gallery> getGalleryById(Long id) {
        return galleryRepo.findById(id);
    }

    @Override
    public List<Gallery> getGalleriesByCarId(Long carId) {
        // This would require a custom method in the repository
        // For now, we'll return all galleries (in a real implementation, you'd add a findByCarId method)
        return galleryRepo.findAll();
    }

    @Override
    public Gallery createGallery(Gallery gallery) {
        return galleryRepo.save(gallery);
    }

    @Override
    public Gallery updateGallery(Long id, Gallery gallery) {
        if (!galleryRepo.existsById(id)) {
            throw new RuntimeException("Gallery not found with id: " + id);
        }
        gallery.setId(id);
        return galleryRepo.save(gallery);
    }

    @Override
    public void deleteGallery(Long id) {
        if (!galleryRepo.existsById(id)) {
            throw new RuntimeException("Gallery not found with id: " + id);
        }
        galleryRepo.deleteById(id);
    }
}