package com.softjourn.vending.entity.listeners;

import com.softjourn.vending.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Provide bean injection into listeners
 */
@Configuration
public class ListenerConfiguration {
    private final ProductImageService productImageService;

    @Autowired
    public ListenerConfiguration(ProductImageService productImageService) {
        this.productImageService = productImageService;
        this.init();
    }

    private void init() {
        ProductImageListener.imageService = productImageService;
    }
}
