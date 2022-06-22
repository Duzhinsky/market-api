package ru.duzhinsky.yandexmegamarket.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "shop_unit")
public class ShopUnitEntity {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    @Column(name = "unit_id", updatable = false, nullable = false)
    private UUID unitId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_till", nullable = true)
    private LocalDateTime validTill;

    @Column(name = "price", nullable = true)
    private Long price;

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private ShopUnitType type;

    @Column(name = "parent", nullable = true)
    private UUID parent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopUnitEntity entity = (ShopUnitEntity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
