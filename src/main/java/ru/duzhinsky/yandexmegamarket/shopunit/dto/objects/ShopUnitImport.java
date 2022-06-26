package ru.duzhinsky.yandexmegamarket.shopunit.dto.objects;

import lombok.*;

/**
 * An object that contain data of an import request item
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of="id")
public class ShopUnitImport {
    private String id;
    private String name;
    private String parentId;
    private String type;
    private Long price;
}
