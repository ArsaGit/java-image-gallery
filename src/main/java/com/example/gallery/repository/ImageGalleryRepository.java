package com.example.gallery.repository;

import com.example.gallery.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageGalleryRepository extends JpaRepository<ImageModel, Long> {
}
