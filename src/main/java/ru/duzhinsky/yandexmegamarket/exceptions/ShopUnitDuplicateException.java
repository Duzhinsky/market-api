package ru.duzhinsky.yandexmegamarket.exceptions;

public class ShopUnitDuplicateException extends Exception {
    public ShopUnitDuplicateException() {
    }

    public ShopUnitDuplicateException(String message) {
        super(message);
    }
}
