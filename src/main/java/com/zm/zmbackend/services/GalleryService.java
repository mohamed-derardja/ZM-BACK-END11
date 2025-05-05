package com.zm.zmbackend.services;

import com.zm.zmbackend.enteties.Gallery;
import java.util.List;
import java.util.Optional;

public interface GalleryService {
    List<Gallery> getAllGalleries();
    Optional<Gallery> getGalleryById(Long id);
    List<Gallery> getGalleriesByCarId(Long carId);
    Gallery createGallery(Gallery gallery);
    Gallery updateGallery(Long id, Gallery gallery);
    void deleteGallery(Long id);
}