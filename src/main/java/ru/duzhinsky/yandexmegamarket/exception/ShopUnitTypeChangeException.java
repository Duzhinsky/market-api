package ru.duzhinsky.yandexmegamarket.exception;

/**
 * The exception is thrown when the request tries to change type of shop unit.
 */
public class ShopUnitTypeChangeException extends BadRequestException {
    public ShopUnitTypeChangeException() {
        super("Change of shop unit type is forbidden");
    }

    public ShopUnitTypeChangeException(String message) {
        super(message);
    }

    public ShopUnitTypeChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShopUnitTypeChangeException(Throwable cause) {
        super(cause);
    }
}
