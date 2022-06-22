package ru.duzhinsky.yandexmegamarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopUnitImportDto that = (ShopUnitImportDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(parentId, that.parentId) && Objects.equals(type, that.type) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentId, type, price);
    }
}
