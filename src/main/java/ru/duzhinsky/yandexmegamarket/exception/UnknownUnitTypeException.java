package ru.duzhinsky.yandexmegamarket.exception;

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
