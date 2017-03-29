package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softjourn.vending.utils.InstantJsonSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class PurchaseProductDto {

    private String name;

    private BigDecimal price;

    @JsonSerialize(using = InstantJsonSerializer.class)
    private Instant time;


}
