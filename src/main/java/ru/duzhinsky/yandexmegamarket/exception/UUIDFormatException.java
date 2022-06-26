package ru.duzhinsky.yandexmegamarket.exception;

/**
 * The exception is thrown when the request contains invalid uuid value
 */
public class UUIDFormatException extends BadRequestException {
    public UUIDFormatException() {
        super("UUID is invalid");
    }

    public UUIDFormatException(String message) {
        super(message);
    }

    public UUIDFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UUIDFormatException(Throwable cause) {
        super(cause);
    }
}
