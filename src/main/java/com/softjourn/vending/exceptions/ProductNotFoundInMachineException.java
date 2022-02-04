package com.softjourn.vending.exceptions;

public class ProductNotFoundInMachineException extends RuntimeException {

  public ProductNotFoundInMachineException(String message) {
    super(message);
  }

  public ProductNotFoundInMachineException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProductNotFoundInMachineException(Throwable cause) {
    super(cause);
  }
}
