package com.softjourn.vending.exceptions;


public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException(Throwable cause) {
        super(cause);
    }

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
