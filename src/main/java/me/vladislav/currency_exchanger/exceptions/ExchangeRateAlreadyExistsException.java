package me.vladislav.currency_exchanger.exceptions;

public class ExchangeRateAlreadyExistsException extends Exception {
    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }

    public ExchangeRateAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
