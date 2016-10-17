package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.utils.InstantJsonSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PurchaseProductDto {

    private String name;

    private BigDecimal price;

    @JsonSerialize(using = InstantJsonSerializer.class)
    private Instant time;

    public PurchaseProductDto(Product product, Instant time) {
        this.setName(product.getName());
        this.setPrice(product.getPrice());
        this.time = time;
    }


}
