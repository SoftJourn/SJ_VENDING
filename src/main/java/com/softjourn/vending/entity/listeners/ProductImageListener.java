package com.softjourn.vending.entity.listeners;

import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.service.ProductImageService;

import javax.persistence.PostRemove;
import java.io.IOException;
import java.util.Optional;

/**
 * React on delete from DB
 * <p>Main goal delete image from file system when record in db deletes</p>
 */
public class ProductImageListener {

    static ProductImageService imageService;

    @PostRemove
    public void productImagePostRemove(ProductImage image) throws IOException {
        Optional
            .ofNullable(imageService)
            .orElseThrow(() -> new IllegalStateException("ListenerConfiguration is not in context"));
        try {
            imageService.deleteFromFileSystem(image.getUrl());
        } catch (Exception e) {
            // TODO log info
        }
    }
}
