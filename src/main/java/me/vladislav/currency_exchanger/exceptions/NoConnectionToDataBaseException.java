package me.vladislav.currency_exchanger.exceptions;

public class NoConnectionToDataBaseException extends Exception {
    public NoConnectionToDataBaseException(String message) {
        super(message);
    }

    public NoConnectionToDataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
