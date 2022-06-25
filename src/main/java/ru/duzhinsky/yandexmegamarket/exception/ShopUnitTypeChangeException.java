package ru.duzhinsky.yandexmegamarket.exception;

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
