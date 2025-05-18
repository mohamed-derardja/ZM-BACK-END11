package com.zm.zmbackend.repositories;

import com.zm.zmbackend.entities.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepo extends JpaRepository<Gallery, Long> {
    /**
     * Find all gallery images for a specific car
     * @param carId the ID of the car
     * @return list of gallery images for the car
     */
    List<Gallery> findByCarId(Long carId);

    /**
     * Find all gallery images for a specific car with pagination
     * @param carId the ID of the car
     * @param pageable pagination information
     * @return page of gallery images for the car
     */
    Page<Gallery> findByCarId(Long carId, Pageable pageable);
}
