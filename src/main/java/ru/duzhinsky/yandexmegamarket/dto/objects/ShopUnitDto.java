package ru.duzhinsky.yandexmegamarket.dto.objects;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
@EqualsAndHashCode(of="id")
public class ShopUnitDto {
    private String id;
    private String name;
    private String date;
    private String parentId;
    private String type;
    private Long price;
    private List<ShopUnitDto> children;
}
