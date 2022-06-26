package ru.duzhinsky.yandexmegamarket.exception;

/**
 * The exception is thrown when name value in the request is null
 */
public class WrongNameException extends BadRequestException {
    public WrongNameException() {
        super("Name value is wrong");
    }

    public WrongNameException(String message) {
        super(message);
    }

    public WrongNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongNameException(Throwable cause) {
        super(cause);
    }
}
