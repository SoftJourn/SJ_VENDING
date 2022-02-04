package com.softjourn.vending.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotImageException extends BadRequestException {

  public NotImageException(String message) {
    super(message);
  }

  public NotImageException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotImageException(Throwable cause) {
    super(cause);
  }

  public NotImageException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
  ) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
