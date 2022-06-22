package ru.duzhinsky.yandexmegamarket.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "valid_till", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTill;

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
        return Objects.equals(id, entity.id) && Objects.equals(unitId, entity.unitId) && Objects.equals(name, entity.name) && Objects.equals(validFrom, entity.validFrom) && Objects.equals(validTill, entity.validTill) && Objects.equals(price, entity.price) && type == entity.type && Objects.equals(parent, entity.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unitId, name, validFrom, validTill, price, type, parent);
    }
}
