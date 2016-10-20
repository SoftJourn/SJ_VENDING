package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softjourn.vending.utils.InstantJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDTO {

    private String account;

    @JsonSerialize(using = InstantJsonSerializer.class)
    private Instant date;

    private String product;

    private BigDecimal price;

}
