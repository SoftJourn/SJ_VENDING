package com.softjourn.vending.enums;

import lombok.Getter;

@Getter
public enum PurchaseDateEnum {

  Any("Any"),
  Today("Today"),
  LastWeek("Last week"),
  LastMonth("Last month"),
  StartDue("Start-Due");

  private final String type;

  PurchaseDateEnum(String type) {
    this.type = type;
  }
}
