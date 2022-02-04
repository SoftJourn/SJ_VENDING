package com.softjourn.vending.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoldProductDTO {

  private String product;
  private Long quantity;

  @Override
  public String toString() {
    return "SoldProductDTO{" +
        "product='" + product + '\'' +
        ", quantity=" + quantity +
        '}';
  }
}
