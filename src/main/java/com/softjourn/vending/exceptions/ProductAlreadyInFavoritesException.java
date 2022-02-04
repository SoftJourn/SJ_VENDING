package com.softjourn.vending.exceptions;

public class ProductAlreadyInFavoritesException extends RuntimeException {

  public ProductAlreadyInFavoritesException(String message) {
    super(message);
  }

  public ProductAlreadyInFavoritesException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProductAlreadyInFavoritesException(Throwable cause) {
    super(cause);
  }
}
