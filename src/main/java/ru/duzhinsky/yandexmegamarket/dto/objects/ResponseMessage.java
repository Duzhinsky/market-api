package ru.duzhinsky.yandexmegamarket.dto.objects;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class ResponseMessage {
    private int code;
    private String message;
}
