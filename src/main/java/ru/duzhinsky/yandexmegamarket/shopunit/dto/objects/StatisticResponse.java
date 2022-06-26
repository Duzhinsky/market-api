package ru.duzhinsky.yandexmegamarket.shopunit.dto.objects;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class StatisticResponse {
    private List<StatisticResponseUnit> items;
}
