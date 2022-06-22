package ru.duzhinsky.yandexmegamarket.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.exceptions.*;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Log
public class ShopUnitImportsService {

    @Autowired
    private ShopUnitRepository unitRepository;

    @Autowired
    private ShopUnitService shopUnitService;

    @Transactional(rollbackFor = Exception.class)
    public void importUnits(ShopUnitImportRequestDto requestDto)
            throws
            WrongDateFormatException,
            WrongParentDataException,
            WrongPriceValueException,
            ShopUnitTypeChangeException,
            ShopUnitDuplicateException,
            WrongNameException
    {
        LocalDateTime importDate = getDateFromDto(requestDto);

        Queue<ShopUnitImportDto> importQueue = new LinkedList<>();
        Map<ShopUnitEntity, Boolean> isCategoryRefreshed = new HashMap<>();
        Set<String> idsSet = new HashSet<>();
        for(var unitDto : requestDto.getItems()) {
            if(idsSet.contains(unitDto.getId()))
                throw new ShopUnitDuplicateException();
            idsSet.add(unitDto.getId());
            importQueue.add(unitDto);
        }

        while (!importQueue.isEmpty()) {
            ShopUnitImportDto node = importQueue.poll();
            if(node.getParentId() != null) {
                if(node.getId().equals(node.getParentId())) throw new WrongParentDataException();
                if(idsSet.contains(node.getParentId())) {
                    importQueue.add(node);
                    continue;
                }
                var parentOpt = unitRepository.findLatestVersion( UUID.fromString(node.getParentId()) );
                if(parentOpt.isEmpty())
                    throw new WrongParentDataException();
                else {
                    var parent = parentOpt.get();
                    if(parent.getType() == ShopUnitType.OFFER)
                        throw new WrongParentDataException();
                    else
                        isCategoryRefreshed.put(parent, false);
                }
            }
            var entity = importNode(node, importDate);
            idsSet.remove(node.getId());
            if(entity.getType() == ShopUnitType.CATEGORY)
                isCategoryRefreshed.put(entity, false);
        }

        var keyset = Set.copyOf(isCategoryRefreshed.keySet());
        for(ShopUnitEntity entity : keyset) {
            updateCategory(entity, importDate, isCategoryRefreshed);
        }
    }

    private void updateCategory(ShopUnitEntity entity, LocalDateTime date, Map<ShopUnitEntity, Boolean> visited) {
        if(visited.get(entity)) return;
        Long price = shopUnitService.calculateAveragePriceForCategory(entity);
        entity.setValidTill(date);
        unitRepository.save(entity);

        ShopUnitEntity newEntity = new ShopUnitEntity();
        newEntity.setUnitId(entity.getUnitId());
        newEntity.setParent(entity.getParent());
        newEntity.setType(entity.getType());
        newEntity.setName(entity.getName());
        newEntity.setPrice(price);
        newEntity.setValidFrom(date);
        unitRepository.save(newEntity);
        visited.put(entity, true);
        entity.setPrice(price);
        if(entity.getParent() != null) {
            var parentOpt = unitRepository.findLatestVersion(entity.getParent());
            parentOpt.ifPresent(parent -> {
                if(!(visited.containsKey(parent) && visited.get(parent))) {
                    visited.put(parent, false);
                    updateCategory(parent, date, visited);
                }
            });
        }
    }

    private ShopUnitEntity importNode(ShopUnitImportDto node, LocalDateTime importDate)
            throws
            WrongPriceValueException,
            ShopUnitTypeChangeException,
            WrongNameException
    {
        if(node.getName() == null)
            throw new WrongNameException();
        if(node.getPrice() != null && node.getPrice() < 0)
            throw new WrongPriceValueException();
        if(node.getPrice() != null && node.getType().equals(ShopUnitType.CATEGORY.toString()))
            throw new WrongPriceValueException();
        if(node.getPrice() == null && node.getType().equals(ShopUnitType.OFFER.toString()))
            throw new WrongPriceValueException();
        var unitEntityOpt = unitRepository.findLatestVersion( UUID.fromString(node.getId()) );
        if(unitEntityOpt.isPresent()) {
            var unitEntity = unitEntityOpt.get();
            if (!node.getType().equals(unitEntity.getType().toString()))
                throw new ShopUnitTypeChangeException();
            unitEntity.setValidTill(importDate);
            unitRepository.save(unitEntity);
        }
        ShopUnitEntity newRecord = toEntity(node);
        newRecord.setValidFrom(importDate);
        return unitRepository.save(newRecord);
    }

    public ShopUnitEntity toEntity(ShopUnitImportDto dto) {
        ShopUnitEntity entity = new ShopUnitEntity();
        entity.setUnitId(UUID.fromString(dto.getId()));
        entity.setName(dto.getName());
        entity.setType(ShopUnitType.valueOf(dto.getType()));
        entity.setPrice(dto.getPrice());
        if(dto.getParentId() != null) {
            var parentOpt = unitRepository.findByUnitIdAndValidTillIsNull(
                    UUID.fromString(dto.getParentId())
            );
            parentOpt.ifPresent(shopUnitEntity -> entity.setParent(shopUnitEntity.getUnitId()));
        }
        return entity;
    }

    private static LocalDateTime getDateFromDto(ShopUnitImportRequestDto dto) throws WrongDateFormatException {
        try {
            return LocalDateTime.parse(dto.getUpdateDate(), DateTimeFormatter.ISO_DATE_TIME);
        } catch(DateTimeParseException e) {
            throw new WrongDateFormatException("Date format does not fits iso 8601");
        }
    }
}













