package com.softjourn.vending.exceptions;


public class NotAvailableException extends RuntimeException{

    public NotAvailableException() {
        super("Item with this ID is not available now.");
    }

}
