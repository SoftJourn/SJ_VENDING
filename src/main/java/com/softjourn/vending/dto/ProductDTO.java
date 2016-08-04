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

    private Position position;

    public ProductDTO(Product product, Position position) {
        this(product);
        this.position = position;
    }

    private ProductDTO(Product product) {
        this.id = product.getId();
        this.price = product.getPrice();
        this.name = product.getName();
        this.imageUrl = product.getImageUrl();
    }
}
