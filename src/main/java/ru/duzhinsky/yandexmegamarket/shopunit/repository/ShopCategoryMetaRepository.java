package ru.duzhinsky.yandexmegamarket.shopunit.repository;

import org.springframework.data.repository.CrudRepository;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopCategoryMetaEntity;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitEntity;

import java.util.UUID;

public interface ShopCategoryMetaRepository extends CrudRepository<ShopCategoryMetaEntity, UUID> {
    void deleteAllByCategory(ShopUnitEntity category);
}
