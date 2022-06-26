package ru.duzhinsky.yandexmegamarket.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyGroup;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * An entity for imported data
 */
@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name="shop_unit")
public class ShopUnitEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private ShopUnitType type;

    @Column(name= "price", nullable = true)
    private Long price;

    @Column(name = "date", nullable = false)
    private LocalDateTime updateDate;

    @JoinColumn(name = "parent", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @LazyGroup("parent")
    private ShopUnitEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyGroup("childrens")
    private List<ShopUnitEntity> childrens;

    @OneToOne(mappedBy = "category", fetch = FetchType.LAZY)
    @LazyGroup("metadata")
    private ShopCategoryMetaEntity metadata;
}
