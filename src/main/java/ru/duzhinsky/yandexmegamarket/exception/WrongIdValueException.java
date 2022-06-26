package ru.duzhinsky.yandexmegamarket.exception;

/**
 * The exception is thrown when id value in the request is null
 */
public class WrongIdValueException extends BadRequestException {
    public WrongIdValueException() {
        super("Wrong id value");
    }

    public WrongIdValueException(String message) {
        super(message);
    }

    public WrongIdValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongIdValueException(Throwable cause) {
        super(cause);
    }
}
