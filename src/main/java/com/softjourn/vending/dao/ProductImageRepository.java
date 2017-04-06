package com.softjourn.vending.dao;

import com.softjourn.vending.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Integer id);
    List<ProductImage> findByProductIdAndIsCover(Integer id, boolean isCover);
    ProductImage findProductImageByUrl(String url);
}
