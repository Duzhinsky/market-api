package ru.duzhinsky.yandexmegamarket.shopunit.dto.objects;

import lombok.*;

import java.util.List;

/**
 * An object that contains an import request.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class ShopUnitImportRequest {
    private List<ShopUnitImport> items;
    private String updateDate;
}
