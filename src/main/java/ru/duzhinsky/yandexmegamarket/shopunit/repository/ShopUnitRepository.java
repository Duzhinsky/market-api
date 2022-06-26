package ru.duzhinsky.yandexmegamarket.shopunit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShopUnitRepository extends CrudRepository<ShopUnitEntity, UUID> {
    List<ShopUnitEntity> findAllByUpdateDateAfter(LocalDateTime updateDate);
}
