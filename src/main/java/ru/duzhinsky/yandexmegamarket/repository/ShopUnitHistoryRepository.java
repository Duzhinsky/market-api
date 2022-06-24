package ru.duzhinsky.yandexmegamarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitHistoryEntity;

import java.util.UUID;

@Repository
public interface ShopUnitHistoryRepository extends CrudRepository<ShopUnitHistoryEntity, UUID> {
    void deleteAllByUnitId(ShopUnitEntity actual);
}
