package ru.duzhinsky.yandexmegamarket.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "shop_unit_history")
public class ShopUnitHistoryEntity {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Column(name="unit_id", nullable = false)
    private UUID unitId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private ShopUnitType type;

    @Column(name= "price", nullable = true)
    private Long price;

    @Column(name = "date", nullable = false)
    private LocalDateTime updateDate;
}
