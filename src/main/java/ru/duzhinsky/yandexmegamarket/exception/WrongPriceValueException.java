package ru.duzhinsky.yandexmegamarket.exception;

public class WrongPriceValueException extends Exception {
    public WrongPriceValueException() {
    }

    public WrongPriceValueException(String message) {
        super(message);
    }
}
