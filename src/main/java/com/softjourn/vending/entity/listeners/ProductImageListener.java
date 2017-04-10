package com.softjourn.vending.entity.listeners;

import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import javax.persistence.PostRemove;

/**
 * React on delete from DB
 * <p>Main goal delete image from file system when record in db deletes</p>
 */
@Configurable(autowire = Autowire.BY_TYPE)
public class ProductImageListener {

    @Autowired
    private ProductImageService imageService;

    @PostRemove
    public void productImagePostRemove(ProductImage image) {
        System.out.println("Listening User Post Remove : ");
    }
}
