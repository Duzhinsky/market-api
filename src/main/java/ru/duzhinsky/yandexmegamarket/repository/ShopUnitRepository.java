package ru.duzhinsky.yandexmegamarket.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopUnitRepository extends CrudRepository<ShopUnitEntity, UUID> {
    Optional<ShopUnitEntity> findByUnitIdAndValidTillIsNull(UUID id);
    List<ShopUnitEntity> findAllByUnitId(UUID unitId);

    default Optional<ShopUnitEntity> findLatestVersion(UUID id) {
        return findByUnitIdAndValidTillIsNull(id);
    }
}