package ru.duzhinsky.yandexmegamarket.exception;

public class WrongDateFormatException extends BadRequestException {
    public WrongDateFormatException() {
        super("The date does not match the format");
    }

    public WrongDateFormatException(String message) {
        super(message);
    }

    public WrongDateFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongDateFormatException(Throwable cause) {
        super(cause);
    }
}
