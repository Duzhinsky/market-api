package ru.duzhinsky.yandexmegamarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class ShopUnitImportRequestDto {
    private List<ShopUnitImportDto> items;
    private String date;
}
