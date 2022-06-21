package ru.duzhinsky.yandexmegamarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;

import java.util.UUID;

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
                entity.getId().toString(),
                entity.getName(),
                entity.getParent() == null ? null : entity.getParent().getId().toString(),
                entity.getType().toString(),
                entity.getPrice()
        );
        return dto;
    }

    public static ShopUnitEntity toEntity(ShopUnitImportDto dto) {
        ShopUnitEntity entity = new ShopUnitEntity();
        entity.setId(UUID.fromString(dto.getId()));
        entity.setName(dto.getName());
        entity.setType(ShopUnitType.valueOf(dto.getType()));
        entity.setPrice(dto.getPrice());
        return entity;
    }
}
