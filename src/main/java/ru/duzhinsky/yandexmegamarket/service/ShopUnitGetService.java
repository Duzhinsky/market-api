package ru.duzhinsky.yandexmegamarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitDto;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.exceptions.ShopUnitNotFoundException;
import ru.duzhinsky.yandexmegamarket.exceptions.UUIDFormatException;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShopUnitGetService {
    @Autowired
    private ShopUnitRepository unitRepository;

    public ShopUnitDto get(String uuid) throws UUIDFormatException, ShopUnitNotFoundException {
        try {
            UUID id = UUID.fromString(uuid);
            var last = unitRepository.findLatestVersion(id);
            if(last.isEmpty())
                throw new ShopUnitNotFoundException();
            return entityToDto(last.get());
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    public ShopUnitDto entityToDto(ShopUnitEntity entity) {
        List<ShopUnitDto> childrens = null;
        if(entity.getType() == ShopUnitType.CATEGORY) {
            childrens = unitRepository
                    .findAllLatestByParent(entity.getUnitId())
                    .stream()
                    .map(this::entityToDto)
                    .collect(Collectors.toList());
        }
        return new ShopUnitDto(
                entity.getUnitId().toString(),
                entity.getName(),
                entity.getValidFrom().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
                entity.getParent() == null ? null : entity.getParent().toString(),
                entity.getType().toString(),
                entity.getPrice(),
                childrens
        );
    }
}
