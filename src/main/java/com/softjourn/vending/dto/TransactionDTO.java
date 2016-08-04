package com.softjourn.vending.dto;


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

    private Instant created;

    private String status;
}
