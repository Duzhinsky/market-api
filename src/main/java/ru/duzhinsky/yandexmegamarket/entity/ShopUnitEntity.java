package ru.duzhinsky.yandexmegamarket.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "shop_unit")
public class ShopUnitEntity {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

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

    @ManyToOne
    @JoinColumn(name="parent")
    private ShopUnitEntity parent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<ShopUnitEntity> childrens;
}
