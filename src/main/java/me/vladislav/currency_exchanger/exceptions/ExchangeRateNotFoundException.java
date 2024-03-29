package me.vladislav.currency_exchanger.exceptions;

public class ExchangeRateNotFoundException extends Exception {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }

    public ExchangeRateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
