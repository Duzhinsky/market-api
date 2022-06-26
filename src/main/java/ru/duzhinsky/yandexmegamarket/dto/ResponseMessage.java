package ru.duzhinsky.yandexmegamarket.dto;

import lombok.*;

/**
 * A data transfer object for responses.
 * Contains a code and a message.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class ResponseMessage {
    private int code;
    private String message;
}
