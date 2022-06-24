package ru.duzhinsky.yandexmegamarket.exception;

public class WrongUnitTypeException extends  Exception {
    public WrongUnitTypeException() {
    }

    public WrongUnitTypeException(String message) {
        super(message);
    }
}
