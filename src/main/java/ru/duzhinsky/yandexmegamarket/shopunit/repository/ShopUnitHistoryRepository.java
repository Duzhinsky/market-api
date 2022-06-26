package ru.duzhinsky.yandexmegamarket.shopunit.repository;

import org.apache.tomcat.jni.Local;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitHistoryEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShopUnitHistoryRepository extends CrudRepository<ShopUnitHistoryEntity, UUID> {
    void deleteAllByUnitId(UUID unitId);
    List<ShopUnitHistoryEntity> findAllByUnitIdAndUpdateDateGreaterThanEqualAndUpdateDateBefore(UUID id, LocalDateTime start, LocalDateTime end);
    default List<ShopUnitHistoryEntity> getStatistics(UUID id, LocalDateTime start, LocalDateTime end) {
        return findAllByUnitIdAndUpdateDateGreaterThanEqualAndUpdateDateBefore(id, start, end);
    }
}
