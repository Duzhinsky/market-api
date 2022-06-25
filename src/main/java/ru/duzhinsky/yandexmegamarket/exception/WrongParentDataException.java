package ru.duzhinsky.yandexmegamarket.exception;

public class WrongParentDataException extends BadRequestException {
    public WrongParentDataException() {
        super("Parent field data is invalid");
    }

    public WrongParentDataException(String message) {
        super(message);
    }

    public WrongParentDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongParentDataException(Throwable cause) {
        super(cause);
    }
}



