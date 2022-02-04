package com.softjourn.vending.exceptions;

public class AlreadyPresentedException extends RuntimeException {

  public AlreadyPresentedException() {
    super("Such item already presented.");
  }

  public AlreadyPresentedException(String message) {
    super(message);
  }

  public AlreadyPresentedException(String message, Throwable cause) {
    super(message, cause);
  }

  public AlreadyPresentedException(Throwable cause) {
    super(cause);
  }

  public AlreadyPresentedException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
  ) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
