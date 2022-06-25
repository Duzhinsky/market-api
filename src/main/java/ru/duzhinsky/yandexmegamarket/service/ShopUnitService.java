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
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
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
    private ShopUnitMapper mapper;

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

        Set<ShopUnitEntity> categoriesPool = new HashSet<>();
        for(String nodeId : presentNodes.keySet()) {
            ShopUnitImport node = presentNodes.get(nodeId);
            String parentId = node.getParentId();
            if(!presentNodes.containsKey(parentId)) {
                if(parentId != null) {
                    var parentOpt = unitRepository.findById(UUID.fromString(parentId));
                    if(parentOpt.isEmpty())
                        throw new WrongParentDataException();
                }
                insertNodes(node, childrens, categoriesPool, importDate);
            }
        }
        saveCategoriesPool(categoriesPool, importDate);
    }

    @Async
    protected void insertNodes(ShopUnitImport node, Map<String, List<ShopUnitImport>> childrens, Set<ShopUnitEntity> categoriesPool, LocalDateTime date)
            throws WrongParentDataException,
            WrongUnitTypeException
    {
        log.info("Insert node: " + node.getName());

        var storedOptional = unitRepository.findById(UUID.fromString(node.getId()));
        if(storedOptional.isPresent() && !storedOptional.get().getType().toString().equals(node.getType()))
            throw new WrongUnitTypeException();

        ShopUnitEntity entity = storedOptional.orElseGet(ShopUnitEntity::new);

        entity.setId(UUID.fromString(node.getId()));
        entity.setName(node.getName());
        entity.setType(ShopUnitType.valueOf(node.getType()));
        entity.setUpdateDate(date);

        if(entity.getType() == ShopUnitType.CATEGORY) {
            if(entity.getParent() != null && !entity.getParent().getId().toString().equals(node.getParentId())) {
                changeCategoryPrice(
                        entity.getParent(),
                        entity.getMetadata().getTotalPrice().negate(),
                        entity.getMetadata().getOffersCount().negate(),
                        categoriesPool,
                        date
                );
            }
            if(node.getParentId() != null) {
                var parentOptional = unitRepository.findById(UUID.fromString(node.getParentId()));
                if(parentOptional.isEmpty()) {
                    throw new WrongParentDataException();
                }
                entity.setParent(parentOptional.get());
                if(!entity.getParent().getId().toString().equals(node.getParentId())) {
                    changeCategoryPrice(
                            parentOptional.get(),
                            entity.getMetadata().getTotalPrice(),
                            entity.getMetadata().getOffersCount(),
                            categoriesPool,
                            date
                    );
                }
            }
        }


        if(entity.getType() == ShopUnitType.OFFER) {
            Long priceDifference = storedOptional.map(e -> node.getPrice() - e.getPrice()).orElseGet(node::getPrice);
            boolean theSame = true;
            if(storedOptional.isPresent() && entity.getParent() != null && !entity.getParent().getId().toString().equals(node.getParentId())) {
                changeCategoryPrice(
                        entity.getParent(),
                        BigInteger.valueOf(-entity.getPrice()),
                        BigInteger.ONE.negate(),
                        categoriesPool,
                        date
                );
                priceDifference = node.getPrice();
                theSame = false;
            }
            entity.setPrice(node.getPrice());
            if(node.getParentId() != null) {
                var parentOptional = unitRepository.findById(UUID.fromString(node.getParentId()));
                if(parentOptional.isEmpty()) {
                    throw new WrongParentDataException();
                }
                if(!parentOptional.get().equals(entity.getParent()))
                    theSame = false;
                entity.setParent(parentOptional.get());
                changeCategoryPrice(
                        entity.getParent(),
                        BigInteger.valueOf(priceDifference),
                        theSame ? BigInteger.ZERO : BigInteger.ONE,
                        categoriesPool,
                        date
                );
            }
        }

        unitRepository.save(entity);
        ShopUnitHistoryEntity history = ShopUnitMapper.toHistoryEntity(entity, date);
        historyRepository.save(history);

        var childsList = childrens.get(node.getId());
        for(ShopUnitImport child : childsList)
            insertNodes(child, childrens, categoriesPool, date);
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

            Set<ShopUnitEntity> categoriesPool = new HashSet<>();
            if(node.getParent() != null)
                changeCategoryPrice(node.getParent(), BigInteger.valueOf(node.getPrice()), BigInteger.valueOf(-1L), categoriesPool, null);
            saveCategoriesPool(categoriesPool, null);
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    private void deleteNode(ShopUnitEntity node) {
        node.getChildrens().forEach(this::deleteNode);
        historyRepository.deleteAllByUnitId(node.getId());
        metaRepository.deleteAllByCategory(node);
        unitRepository.delete(node);
    }

    @Transactional(rollbackFor = Exception.class)
    public ShopUnitDto get(String uuid)
            throws UUIDFormatException,
            ShopUnitNotFoundException
    {
        try {
            UUID id = UUID.fromString(uuid);
            var node = unitRepository.findById(id);
            if(node.isEmpty())
                throw new ShopUnitNotFoundException();
            return mapper.toDto(node.get());
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    private void changeCategoryPrice(ShopUnitEntity category, BigInteger priceChange, BigInteger countChange, Set<ShopUnitEntity> categoriesPool, LocalDateTime date) {
        categoriesPool.add(category);
        ShopCategoryMetaEntity meta = null;
        if(category.getMetadata() == null) {
            meta = new ShopCategoryMetaEntity();
            meta.setTotalPrice(BigInteger.ZERO);
            meta.setOffersCount(BigInteger.ZERO);
            meta.setCategory(category);
            meta = metaRepository.save(meta);
            category.setMetadata(meta);
        } else {
            meta = category.getMetadata();
        }
        meta.setTotalPrice(meta.getTotalPrice().add(priceChange));
        meta.setOffersCount(meta.getOffersCount().add(countChange));
        Long avPrice = null;
        if(!Objects.equals(meta.getOffersCount(), BigInteger.ZERO))
            avPrice = meta.getTotalPrice().divide(meta.getOffersCount()).longValue();
        category.setPrice(avPrice);
        if(category.getParent() != null)
            changeCategoryPrice(category.getParent(), priceChange, countChange, categoriesPool, date);
    }

    private void saveCategoriesPool(Set<ShopUnitEntity> categoriesPool, LocalDateTime date) {
        for(ShopUnitEntity category : categoriesPool) {
            if(date != null) category.setUpdateDate(date);
            unitRepository.save(category);
            if(date != null) {
                ShopUnitHistoryEntity history = ShopUnitMapper.toHistoryEntity(category, date);
                historyRepository.save(history);
            }
        }
    }
}
