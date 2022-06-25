package ru.duzhinsky.yandexmegamarket.exception;

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
