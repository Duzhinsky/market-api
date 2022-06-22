package ru.duzhinsky.yandexmegamarket.exceptions;

public class UUIDFormatException extends Exception{
    public UUIDFormatException() {
    }

    public UUIDFormatException(String message) {
        super(message);
    }
}
