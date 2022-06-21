package ru.duzhinsky.yandexmegamarket.exceptions;

public class WrongPriceValueException extends Exception {
    public WrongPriceValueException() {
    }

    public WrongPriceValueException(String message) {
        super(message);
    }
}
