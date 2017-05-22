package com.softjourn.vending.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadDetailsDTO {

    private String productName;

    private BigDecimal price;

    private String cell;

    private Integer count;

    private BigDecimal total;

}
