package ru.duzhinsky.yandexmegamarket.dto.objects;

import lombok.*;

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
