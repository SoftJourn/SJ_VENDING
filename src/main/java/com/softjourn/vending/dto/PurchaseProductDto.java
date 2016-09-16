package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.utils.InstantJsonSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PurchaseProductDto extends Product {

    @JsonSerialize(using = InstantJsonSerializer.class)
    private Instant time;

    public PurchaseProductDto(Product product, Instant time) {
        this.setId(product.getId());
        this.setPrice(product.getPrice());
        this.setName(product.getName());
        this.setImageUrl(product.getImageUrl());
        this.setCategory(product.getCategory());
        this.setDescription(product.getDescription());
        this.time = time;
    }


}
