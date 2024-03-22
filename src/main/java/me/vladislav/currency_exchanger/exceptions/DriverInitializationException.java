package me.vladislav.currency_exchanger.exceptions;

public class DriverInitializationException extends Exception {
    public DriverInitializationException(String message) {
        super(message);
    }

    public DriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
