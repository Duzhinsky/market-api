package ru.duzhinsky.yandexmegamarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;

@AllArgsConstructor
@Getter @Setter
public class ShopUnitImportDto {
    private String id;
    private String name;
    private String parentId;
    private String type;
    private Long price;

    public static ShopUnitImportDto toDto(ShopUnitEntity entity) {
        ShopUnitImportDto dto = new ShopUnitImportDto(
                entity.getUnitId().toString(),
                entity.getName(),
                entity.getParent() == null ? null : entity.getParent().toString(),
                entity.getType().toString(),
                entity.getPrice()
        );
        return dto;
    }
}
