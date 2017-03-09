package com.softjourn.vending.service;

import com.softjourn.vending.dao.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ImageService {

    final ImageRepository repository;

    @Autowired
    public ImageService(ImageRepository repository) {
        this.repository = repository;
    }


}
