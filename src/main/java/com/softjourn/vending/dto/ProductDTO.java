package com.softjourn.vending.dto;


import com.softjourn.vending.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    private Integer id;

    private BigDecimal price;

    private String name;

    private String imageUrl;

    private String description;

    private String category;

    private Position position;

    public ProductDTO(Product product, Position position) {
        this(product);
        this.position = position;
    }

    private ProductDTO(Product product) {
        this.setId(product.getId());
        this.setPrice(product.getPrice());
        this.setName(product.getName());
//        this.setImageUrl(product.getImageUrl());
        this.setCategory(product.getCategory().getName());
        this.setDescription(product.getDescription());
    }
}
