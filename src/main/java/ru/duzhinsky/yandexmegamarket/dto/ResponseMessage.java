package ru.duzhinsky.yandexmegamarket.dto;

import lombok.*;

/**
 * A data transfer object for responses.
 * Contains a code and a message.
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ResponseMessage {
    private final int code;
    private final String message;
}
