package com.softjourn.vending.exceptions;

public class ProductIsNotInFavoritesException extends RuntimeException {

    public ProductIsNotInFavoritesException(String message) {
        super(message);
    }

    public ProductIsNotInFavoritesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductIsNotInFavoritesException(Throwable cause) {
        super(cause);
    }


}
