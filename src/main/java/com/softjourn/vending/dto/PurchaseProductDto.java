package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.utils.InstantJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PurchaseProductDto {

    private Integer id;

    @JsonSerialize(using = InstantJsonSerializer.class)
    private Instant time;

    public PurchaseProductDto(Product product, Instant time) {
        this.setId(product.getId());
        this.time = time;
    }


}
