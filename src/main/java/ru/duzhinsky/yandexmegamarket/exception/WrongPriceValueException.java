package ru.duzhinsky.yandexmegamarket.exception;

public class WrongPriceValueException extends BadRequestException {
    public WrongPriceValueException() {
        super("Wrong price value");
    }

    public WrongPriceValueException(String message) {
        super(message);
    }

    public WrongPriceValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongPriceValueException(Throwable cause) {
        super(cause);
    }
}
