package com.softjourn.vending.exceptions;

public class VendingProcessingException extends RuntimeException {

  public VendingProcessingException(String message) {
    super(message);
  }

  public VendingProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
