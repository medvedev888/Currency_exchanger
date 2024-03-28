package me.vladislav.currency_exchanger.exceptions;

public class CurrencyCodeAlreadyExistsException extends Exception {
    public CurrencyCodeAlreadyExistsException(String message) {
        super(message);
    }

    public CurrencyCodeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
