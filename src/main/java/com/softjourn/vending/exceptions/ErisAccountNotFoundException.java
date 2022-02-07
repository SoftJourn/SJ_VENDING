package com.softjourn.vending.exceptions;

public class ErisAccountNotFoundException extends RuntimeException {

  public ErisAccountNotFoundException(String accountName) {
    super(
        String.format("Can't create account for %s. There is no free eris accounts", accountName));
  }
}
