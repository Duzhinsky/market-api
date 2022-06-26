package ru.duzhinsky.yandexmegamarket.exception;

/**
 * The exception is thrown when the request contains duplicate of an item
 */
public class ShopUnitDuplicateException extends BadRequestException {
    public ShopUnitDuplicateException() {
        super("Dupticate units are forbidden");
    }

    public ShopUnitDuplicateException(String message) {
        super(message);
    }

    public ShopUnitDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
