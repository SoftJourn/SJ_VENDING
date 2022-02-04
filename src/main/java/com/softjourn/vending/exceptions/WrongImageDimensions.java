package com.softjourn.vending.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WrongImageDimensions extends RuntimeException {

  public WrongImageDimensions(String message) {
    super(message);
  }

  public WrongImageDimensions(String message, Throwable cause) {
    super(message, cause);
  }

  public WrongImageDimensions(Throwable cause) {
    super(cause);
  }
}
