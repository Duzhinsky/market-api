package ru.duzhinsky.yandexmegamarket.exception;

public class WrongIdValueException extends Exception {
    public WrongIdValueException() {
        super("Wrong id value");
    }

    public WrongIdValueException(String message) {
        super(message);
    }
}
