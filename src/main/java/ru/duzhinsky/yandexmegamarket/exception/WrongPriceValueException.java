package ru.duzhinsky.yandexmegamarket.exception;

/**
 * The exception is thrown when price value in the request is invalid
 * Could be caused by the following reasons:
 * - price is negative
 * - price is null for offers
 * - price is not null for categories
 */
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
