package com.softjourn.vending.exceptions;

public class NotEnoughAmountException extends PaymentProcessingException {

  public NotEnoughAmountException() {
    super("Not enough amount of coins in account to by this item.");
  }
}
