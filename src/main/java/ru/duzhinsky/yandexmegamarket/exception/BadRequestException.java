package ru.duzhinsky.yandexmegamarket.exception;

/**
 * BadRequestExceptions is the superclass of those exceptions
 * that can be thrown during request processing and are thrown
 * due to an error in a request.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("Bad request");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
