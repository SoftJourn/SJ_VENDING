package com.softjourn.vending.entity.listeners;

import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.Resource;
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
        imageService.deleteFromFileSystem(image.getUrl());
    }
}
