package com.softjourn.vending.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.softjourn.vending.utils.InstantJsondeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionDTO {

    private Long id;

    private String account;

    private String destination;

    private BigDecimal amount;

    private String comment;

    @JsonDeserialize(using = InstantJsondeserializer.class)
    private Instant created;

    private String status;

    private BigDecimal remain;

    private String error;
}
