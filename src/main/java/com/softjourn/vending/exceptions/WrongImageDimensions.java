package com.softjourn.vending.exceptions;

public class WrongImageDimensions extends RuntimeException {

    public WrongImageDimensions() {
    }

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
