package ru.duzhinsky.yandexmegamarket.dto.objects;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class ShopUnitImportRequest {
    private List<ShopUnitImport> items;
    private String updateDate;
}
