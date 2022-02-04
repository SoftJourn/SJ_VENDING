package com.softjourn.vending.dto;

import lombok.Data;

@Data
public class DashboardDTO {

  private Long products;
  private Long machines;
  private Long categories;
  private Long purchases;
}
