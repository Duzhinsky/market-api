package ru.duzhinsky.yandexmegamarket.exception;

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
