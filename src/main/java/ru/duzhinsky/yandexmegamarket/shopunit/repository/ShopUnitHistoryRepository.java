package ru.duzhinsky.yandexmegamarket.shopunit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitHistoryEntity;

import java.util.UUID;

@Repository
public interface ShopUnitHistoryRepository extends CrudRepository<ShopUnitHistoryEntity, UUID> {
    void deleteAllByUnitId(UUID unitId);
}
