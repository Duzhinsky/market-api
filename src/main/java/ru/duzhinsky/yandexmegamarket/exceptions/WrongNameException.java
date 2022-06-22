package ru.duzhinsky.yandexmegamarket.exceptions;

public class WrongNameException extends Exception {
    public WrongNameException() {
    }

    public WrongNameException(String message) {
        super(message);
    }
}
