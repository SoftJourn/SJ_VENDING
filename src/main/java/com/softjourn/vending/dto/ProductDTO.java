package com.softjourn.vending.dto;


import com.softjourn.vending.entity.Product;
import lombok.Data;

@Data
public class ProductDTO extends Product {

    private Position position;

    public ProductDTO(Product product, Position position) {
        this(product);
        this.position = position;
    }

    private ProductDTO(Product product) {
        this.setId(product.getId());
        this.setPrice(product.getPrice());
        this.setName(product.getName());
        this.setImageUrl(product.getImageUrl());
        this.setCategory(product.getCategory());
        this.setDescription(product.getDescription());
    }
}
