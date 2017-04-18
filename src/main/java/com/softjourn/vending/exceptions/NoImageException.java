package com.softjourn.vending.exceptions;


public class NoImageException extends RuntimeException {

    public NoImageException() {
    }

    public NoImageException(String message) {
        super(message);
    }

    public NoImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoImageException(Throwable cause) {
        super(cause);
    }

    public NoImageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
