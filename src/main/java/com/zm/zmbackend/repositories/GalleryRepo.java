package com.zm.zmbackend.repositories;

import com.zm.zmbackend.enteties.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepo extends JpaRepository<Gallery, Long> {
}
