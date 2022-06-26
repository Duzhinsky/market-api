package ru.duzhinsky.yandexmegamarket.shopunit.exception;

/**
 * The exception is thrown when a requested item was not found
 */
public class ShopUnitNotFoundException extends BadRequestException {
    public ShopUnitNotFoundException() {
        super("Shop unit was not found");
    }

    public ShopUnitNotFoundException(String message) {
        super(message);
    }

    public ShopUnitNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShopUnitNotFoundException(Throwable cause) {
        super(cause);
    }
}
