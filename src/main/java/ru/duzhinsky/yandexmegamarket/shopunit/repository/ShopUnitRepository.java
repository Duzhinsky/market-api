package ru.duzhinsky.yandexmegamarket.shopunit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitEntity;

import java.util.UUID;

@Repository
public interface ShopUnitRepository extends CrudRepository<ShopUnitEntity, UUID> {
}
