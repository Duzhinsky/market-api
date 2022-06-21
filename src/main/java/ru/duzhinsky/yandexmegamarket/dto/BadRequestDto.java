package ru.duzhinsky.yandexmegamarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Getter @Setter
public class BadRequestDto {
    private static final int code = 400;
    private String message;
}
