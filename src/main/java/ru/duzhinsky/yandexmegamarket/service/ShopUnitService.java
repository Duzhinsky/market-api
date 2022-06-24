package ru.duzhinsky.yandexmegamarket.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duzhinsky.yandexmegamarket.dto.mappers.ShopUnitMapper;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitDto;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImport;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.entity.ShopCategoryMetaEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitHistoryEntity;
import ru.duzhinsky.yandexmegamarket.exception.*;
import ru.duzhinsky.yandexmegamarket.repository.ShopCategoryMetaRepository;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitHistoryRepository;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log
public class ShopUnitService {
    @Autowired
    private ShopUnitRepository unitRepository;

    @Autowired
    private ShopUnitHistoryRepository historyRepository;

    @Autowired
    private ShopCategoryMetaRepository metaRepository;

    @Autowired
    private ShopUnitMapper unitMapper;

    @Transactional(rollbackFor = Exception.class)
    public void importUnits(ShopUnitImportRequest requestDto)
            throws WrongDateFormatException,
            WrongUnitTypeException,
            WrongNameException,
            WrongIdValueException,
            WrongPriceValueException,
            WrongParentDataException
    {
        LocalDateTime importDate = ShopUnitMapper.getDateFromDto(requestDto);
        for(var importDto : requestDto.getItems())
            ShopUnitMapper.validateImportDto(importDto);

        Map<String, List<ShopUnitImport>> childrens = new HashMap<>();
        Map<String, ShopUnitImport> presentNodes = new HashMap<>();

        for(ShopUnitImport node : requestDto.getItems()) {
            presentNodes.put(node.getId(), node);
            childrens.computeIfAbsent(node.getParentId(), k -> new ArrayList<>());
            childrens.computeIfAbsent(node.getId(), k -> new ArrayList<>());
            if(node.getParentId() != null) {
                childrens.get(node.getParentId()).add(node);
            }
        }

        Set<String> categoriesIdToRefresh = new HashSet<>();
        for(String nodeId : presentNodes.keySet()) {
            ShopUnitImport node = presentNodes.get(nodeId);
            String parentId = node.getParentId();
            if(!presentNodes.containsKey(parentId)) {
                if(parentId != null) {
                    var parentOpt = unitRepository.findById(UUID.fromString(parentId));
                    if(parentOpt.isEmpty())
                        throw new WrongParentDataException();
                }
                insertNodes(node, childrens, importDate);
            }
        }
    }

    @Async
    protected void insertNodes(ShopUnitImport node, Map<String, List<ShopUnitImport>> chilnrens, LocalDateTime date)
            throws WrongParentDataException
    {
        log.info("Insert node: " + node.getName());

        Long priceDifference = 0L;

        var storedOptional = unitRepository.findById(UUID.fromString(node.getId()));
        if(storedOptional.isPresent())
            priceDifference = node.getPrice()-storedOptional.get().getPrice();
        else
            priceDifference = node.getPrice();
        storedOptional.ifPresent(e -> unitRepository.delete(e));
        
        ShopUnitEntity entity = unitMapper.toEntity(node, date);
        unitRepository.save(entity);
        ShopUnitHistoryEntity history = ShopUnitMapper.toHistoryEntity(entity, date);
        historyRepository.save(history);

        if(entity.getParent() != null)
            changeCategoryPrice(entity.getParent(), priceDifference, storedOptional.isPresent() ? 0 : 1L);
        
        var childsList = chilnrens.get(node.getId());
        for(ShopUnitImport child : childsList)
            insertNodes(child, chilnrens, date);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String uuid)
            throws UUIDFormatException,
            ShopUnitNotFoundException
    {
        try {
            UUID id = UUID.fromString(uuid);
            var nodeOpt = unitRepository.findById(id);
            if (nodeOpt.isEmpty())
                throw new ShopUnitNotFoundException();
            var node = nodeOpt.get();
            deleteNode(node);
            if(node.getParent() != null)
                changeCategoryPrice(node.getParent(), node.getPrice(), -1L);
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    private void deleteNode(ShopUnitEntity node) {
        node.getChildrens().forEach(this::deleteNode);
        historyRepository.deleteAllByUnitId(node);
        unitRepository.delete(node);
    }

    public ShopUnitDto get(String uuid)
            throws UUIDFormatException,
            ShopUnitNotFoundException
    {
        try {
            UUID id = UUID.fromString(uuid);
            var node = unitRepository.findById(id);
            if(node.isEmpty())
                throw new ShopUnitNotFoundException();
            return ShopUnitMapper.toDto(node.get());
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    private void changeCategoryPrice(ShopUnitEntity category, Long priceChange, Long countChange) {
        ShopCategoryMetaEntity meta = null;
        if(category.getMetadata() == null) {
            meta = new ShopCategoryMetaEntity();
            meta.setTotalPrice(BigInteger.ZERO);
            meta.setOffersCount(BigInteger.ZERO);
            meta.setCategory(category);
            metaRepository.save(meta);
            category.setMetadata(meta);
        } else {
            meta = category.getMetadata();
        }
        meta.setTotalPrice(meta.getTotalPrice().add(BigInteger.valueOf(priceChange)));
        meta.setOffersCount(meta.getOffersCount().add(BigInteger.valueOf(countChange)));
        Long avPrice = meta.getTotalPrice().divide(meta.getOffersCount()).longValue();
        category.setPrice(avPrice);
        unitRepository.save(category);
        if(category.getParent() != null)
            changeCategoryPrice(category.getParent(), priceChange, countChange);
    }
}
