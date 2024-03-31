package me.vladislav.currency_exchanger.exceptions;

public class IncorrectInputException extends Exception {
    public IncorrectInputException(String message) {
        super(message);
    }

    public IncorrectInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
