package ru.duzhinsky.yandexmegamarket.exceptions;

public class WrongDateFormatException extends Exception {
    public WrongDateFormatException() {
    }

    public WrongDateFormatException(String message) {
        super(message);
    }
}
