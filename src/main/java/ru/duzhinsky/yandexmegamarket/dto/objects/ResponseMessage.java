package ru.duzhinsky.yandexmegamarket.dto.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ResponseMessage {
    private int code;
    private String message;
}
