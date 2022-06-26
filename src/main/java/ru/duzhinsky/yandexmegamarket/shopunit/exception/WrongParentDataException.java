package ru.duzhinsky.yandexmegamarket.shopunit.exception;

/**
 * The exception is thrown when parent value in the request is invalid.
 * It could happen in the following cases:
 * - parent unit does not exist
 * - parent is an offer
 * - a cycle is being formed
 */
public class WrongParentDataException extends BadRequestException {
    public WrongParentDataException() {
        super("Parent field data is invalid");
    }

    public WrongParentDataException(String message) {
        super(message);
    }

    public WrongParentDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongParentDataException(Throwable cause) {
        super(cause);
    }
}



