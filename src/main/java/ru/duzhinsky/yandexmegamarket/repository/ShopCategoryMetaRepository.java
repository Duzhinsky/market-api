package ru.duzhinsky.yandexmegamarket.repository;

import org.springframework.data.repository.CrudRepository;
import ru.duzhinsky.yandexmegamarket.entity.ShopCategoryMetaEntity;

import java.util.UUID;

public interface ShopCategoryMetaRepository extends CrudRepository<ShopCategoryMetaEntity, UUID> {
}
