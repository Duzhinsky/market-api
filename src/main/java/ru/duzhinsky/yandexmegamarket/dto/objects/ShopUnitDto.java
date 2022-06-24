package ru.duzhinsky.yandexmegamarket.dto.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class ShopUnitDto {
    private String id;
    private String name;
    private String date;
    private String parentId;
    private String type;
    private Long price;
    private List<ShopUnitDto> children;
}