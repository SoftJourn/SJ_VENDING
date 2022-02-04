package com.softjourn.vending.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValueIsNotSet extends RuntimeException {

  public ValueIsNotSet(String message) {
    super(message);
  }

  public ValueIsNotSet(Throwable cause) {
    super(cause);
  }
}
