package com.zm.zmbackend.services;

import com.zm.zmbackend.entities.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface GalleryService {
    List<Gallery> getAllGalleries();
    Page<Gallery> getAllGalleriesPaged(Pageable pageable);

    Optional<Gallery> getGalleryById(Long id);

    List<Gallery> getGalleriesByCarId(Long carId);
    Page<Gallery> getGalleriesByCarIdPaged(Long carId, Pageable pageable);

    Gallery createGallery(Gallery gallery);
    Gallery updateGallery(Long id, Gallery gallery);
    void deleteGallery(Long id);
}
