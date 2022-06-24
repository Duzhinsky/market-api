package ru.duzhinsky.yandexmegamarket.exception;

public class WrongNameException extends Exception {
    public WrongNameException() {
    }

    public WrongNameException(String message) {
        super(message);
    }
}
