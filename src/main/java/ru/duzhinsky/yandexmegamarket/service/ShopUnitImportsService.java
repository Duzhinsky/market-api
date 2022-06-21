package ru.duzhinsky.yandexmegamarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.exceptions.ShopUnitTypeChangeException;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongParentDataException;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongDateFormatException;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongPriceValueException;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@Service
public class ShopUnitImportsService {

    @Autowired
    private ShopUnitRepository unitRepository;

    @Transactional
    @Async
    public void importUnits(ShopUnitImportRequestDto requestDto)
            throws
            WrongDateFormatException,
            WrongParentDataException,
            WrongPriceValueException,
            ShopUnitTypeChangeException
    {
        Date importDate = getDateFromDto(requestDto);

        // Поскольку порядок не гарантирован, нужно импортировать по ходу добавления потомков
        Queue<ShopUnitImportDto> importQueue = new LinkedList<>();
        Set<String> idsSet = new HashSet<>();
        requestDto.getItems().forEach(unitDto -> {
            idsSet.add(unitDto.getId());
            importQueue.add(unitDto);
        });

        while (!importQueue.isEmpty()) {
            ShopUnitImportDto node = importQueue.poll();
            if(node.getParentId() != null) {
                if(node.getId().equals(node.getParentId())) throw new WrongParentDataException();
                if(idsSet.contains(node.getParentId())) {
                    importQueue.add(node);
                    continue;
                }
                if(unitRepository.findLatestVersion( UUID.fromString(node.getId()) ).isEmpty())
                    throw new WrongParentDataException();
            }
            importNode(node, importDate);
            idsSet.remove(node.getParentId());
        }

    }

    private void importNode(ShopUnitImportDto node, Date importDate)
            throws
            WrongPriceValueException,
            ShopUnitTypeChangeException
    {
        if(node.getPrice() < 0) throw new WrongPriceValueException();
        var unitEntityOpt = unitRepository.findLatestVersion( UUID.fromString(node.getId()) );
        if(unitEntityOpt.isPresent()) {
            var unitEntity = unitEntityOpt.get();
            if (!node.getType().equals(unitEntity.getType().toString()))
                throw new ShopUnitTypeChangeException();
            unitEntity.setValidTill(importDate);
            unitRepository.save(unitEntity);
        };
        ShopUnitEntity newRecord = ShopUnitImportDto.toEntity(node);
        newRecord.setValidFrom(importDate);
        unitRepository.save(newRecord);
    }

    private static Date getDateFromDto(ShopUnitImportRequestDto dto) throws WrongDateFormatException {
        try {
            TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(dto.getUpdateDate());
            Instant i = Instant.from(ta);
            return Date.from(i);
        } catch(DateTimeParseException e) {
            throw new WrongDateFormatException("Date format does not fits iso 8601");
        }
    }
}













