package com.softjourn.vending.exceptions;

public class ValueIsNotSet extends RuntimeException {

    public ValueIsNotSet() {
    }

    public ValueIsNotSet(String message) {
        super(message);
    }

    public ValueIsNotSet(Throwable cause) {
        super(cause);
    }
}
