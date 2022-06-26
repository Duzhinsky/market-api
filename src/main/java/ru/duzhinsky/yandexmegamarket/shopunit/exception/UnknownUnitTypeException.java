package ru.duzhinsky.yandexmegamarket.shopunit.exception;

/**
 * The exception is thrown when a unit type from the request is unknown.
 */
public class UnknownUnitTypeException extends BadRequestException {
    public UnknownUnitTypeException() {
        super("The unit type is unknown");
    }

    public UnknownUnitTypeException(String message) {
        super(message);
    }

    public UnknownUnitTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownUnitTypeException(Throwable cause) {
        super(cause);
    }
}
