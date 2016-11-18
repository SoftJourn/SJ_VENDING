package com.softjourn.vending.exceptions;

public class MachineNotFoundException extends RuntimeException {

    public MachineNotFoundException(String message) {
        super(message);
    }

    public MachineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineNotFoundException(Throwable cause) {
        super(cause);
    }

}
