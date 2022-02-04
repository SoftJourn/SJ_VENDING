package com.softjourn.vending.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.softjourn.vending.utils.InstantJsonSerializer;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadHistoryResponseDTO {

  private BigDecimal total;

  @JsonProperty("date")
  @JsonSerialize(using = InstantJsonSerializer.class)
  private Instant dateAdded;
  private String productName;
  private BigDecimal productPrice;
  private String cell;
  private Integer count;
}
