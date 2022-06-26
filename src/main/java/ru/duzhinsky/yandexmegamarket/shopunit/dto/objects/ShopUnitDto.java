package ru.duzhinsky.yandexmegamarket.shopunit.dto.objects;

import lombok.*;

import java.util.List;

/**
 * A data transfer object of a shop unit.
 * Used as a response to a GET request.
 */
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
