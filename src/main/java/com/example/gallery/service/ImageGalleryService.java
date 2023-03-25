package com.example.gallery.service;

import com.example.gallery.model.ImageModel;
import com.example.gallery.repository.ImageGalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageGalleryService {

    @Autowired
    private ImageGalleryRepository imageGalleryRepository;

    public void saveImage(ImageModel imageModel) {
        imageGalleryRepository.save(imageModel);
    }

    public void changeName(Long id, String name) {
        ImageModel image = imageGalleryRepository.findById(id).get();
        image.setName(name);
        imageGalleryRepository.save(image);
    }

    public List<ImageModel> getAllActiveImages() {
        return imageGalleryRepository.findAll();
    }

    public Optional<ImageModel> getImageById(Long id) {
        return imageGalleryRepository.findById(id);
    }
}

