package com.softjourn.vending.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.softjourn.vending.utils.InstantJsonDeserializer;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class TransactionDTO {

  private Long id;
  private String account;
  private String destination;
  private BigDecimal amount;
  private String comment;

  @JsonDeserialize(using = InstantJsonDeserializer.class)
  private Instant created;
  private String status;
  private BigDecimal remain;
  private String error;

  @Override
  public String toString() {
    return "TransactionDTO{" +
        "id=" + id +
        ", account='" + account + '\'' +
        ", destination='" + destination + '\'' +
        ", amount=" + amount.toString() +
        ", comment='" + comment + '\'' +
        ", created=" + created +
        ", status='" + status + '\'' +
        ", remain=" + remain +
        ", error='" + error + '\'' +
        '}';
  }
}
