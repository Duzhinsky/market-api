package ru.duzhinsky.yandexmegamarket.shopunit.dto.objects;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of="id")
public class StatisticResponseUnit {
    private String id;
    private String name;
    private String date;
    private String parentId;
    private Long price;
    private String type;
}
