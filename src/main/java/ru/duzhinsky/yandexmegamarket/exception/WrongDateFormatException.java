package ru.duzhinsky.yandexmegamarket.exception;

public class WrongDateFormatException extends Exception {
    public WrongDateFormatException() {
    }

    public WrongDateFormatException(String message) {
        super(message);
    }
}
