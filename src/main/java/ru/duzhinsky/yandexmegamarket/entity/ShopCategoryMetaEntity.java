package ru.duzhinsky.yandexmegamarket.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.UUID;

/**
 * An entity for storing metadata of a category.
 * Used to avoid traversing the entire subtree (childs of category) after each change.
 */
@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "category_metadata")
public class ShopCategoryMetaEntity {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", referencedColumnName = "id")
    private ShopUnitEntity category;

    @Column(name = "total_price")
    private BigInteger totalPrice;

    @Column(name = "offers_count")
    private BigInteger offersCount;
}
