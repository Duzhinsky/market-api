package ru.duzhinsky.yandexmegamarket.shopunit;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.mappers.ShopUnitMapper;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitDto;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImport;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.StatisticResponse;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopCategoryMetaEntity;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.*;
import ru.duzhinsky.yandexmegamarket.shopunit.repository.ShopCategoryMetaRepository;
import ru.duzhinsky.yandexmegamarket.shopunit.repository.ShopUnitHistoryRepository;
import ru.duzhinsky.yandexmegamarket.shopunit.repository.ShopUnitRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log
public class ShopUnitService {

    private final ShopUnitRepository unitRepository;
    private final ShopUnitHistoryRepository historyRepository;
    private final ShopCategoryMetaRepository metaRepository;
    private final ShopUnitMapper mapper;

    @Autowired
    public ShopUnitService(ShopUnitRepository unitRepository, ShopUnitHistoryRepository historyRepository, ShopCategoryMetaRepository metaRepository, ShopUnitMapper mapper) {
        this.unitRepository = unitRepository;
        this.historyRepository = historyRepository;
        this.metaRepository = metaRepository;
        this.mapper = mapper;
    }

    /**
     * Handles import requests.
     * @param requestDto data transfer object of the request
     * @throws ShopUnitDuplicateException if a unit stated more than once
     * @throws ShopUnitTypeChangeException if request tries to change the type of an existent unit
     * @throws WrongDateFormatException if date format does not match ISO 8601
     * @throws WrongIdValueException if id is null
     * @throws WrongNameException if name is null
     * @throws WrongPriceValueException if price value is invalid
     * @throws UnknownUnitTypeException if unit type is unknown
     * @throws WrongParentDataException if id is equal to parent id or parent does not exists
     */
    @Transactional(rollbackFor = Exception.class)
    public void importUnits(ShopUnitImportRequest requestDto) {
        LocalDateTime importDate = ShopUnitMapper.getDate(requestDto.getUpdateDate());
        for(var importDto : requestDto.getItems())
            ShopUnitMapper.validateImportDto(importDto);

        // Adjacency list of the units hierarchy
        Map<String, List<ShopUnitImport>> childrens = new ConcurrentHashMap<>();
        Map<String, ShopUnitImport> presentNodes = new HashMap<>();

        for(ShopUnitImport node : requestDto.getItems()) {
            if(presentNodes.containsKey(node.getId()))
                throw new ShopUnitDuplicateException();
            presentNodes.put(node.getId(), node);
            childrens.computeIfAbsent(node.getId(), k -> new ArrayList<>());
            if(node.getParentId() != null)
                childrens.computeIfAbsent(node.getParentId(), k -> new ArrayList<>());
            if(node.getParentId() != null) {
                childrens.get(node.getParentId()).add(node);
            }
        }

        // Categories pool is a set of categories that were affected during import
        // I use it because each category could be affected many times
        // Some of affected categories is not stated in the request
        Set<ShopUnitEntity> categoriesPool = Collections.newSetFromMap(new ConcurrentHashMap<>());
        // the cycle is looking for the roots of connected components and inserts their subtrees
        for(String nodeId : presentNodes.keySet()) {
            ShopUnitImport node = presentNodes.get(nodeId);
            String parentId = node.getParentId();
            if(!presentNodes.containsKey(parentId)) {
                insertNodeSubtree(node, childrens, categoriesPool, importDate);
            }
        }
        saveCategoriesPool(categoriesPool, importDate);
    }

    /**
     * Handles delete requests.
     * @param uuid id of the unit to delete
     * @throws UUIDFormatException if uuid string can't be converted to UUID
     * @throws ShopUnitNotFoundException if unit with such uuid was not found
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uuid) {
        try {
            UUID id = UUID.fromString(uuid);
            var nodeOpt = unitRepository.findById(id);
            if (nodeOpt.isEmpty())
                throw new ShopUnitNotFoundException();
            var node = nodeOpt.get();
            deleteNode(node);

            // Since we have changed the subtree, we have to update the higher-order categories
            if(node.getParent() != null) {
                Set<ShopUnitEntity> categoriesPool = new HashSet<>();
                if(node.getType() == ShopUnitType.OFFER)
                    onCategoryOfferChanged(node.getParent(), BigInteger.valueOf(-node.getPrice()), BigInteger.valueOf(-1L), categoriesPool);
                else if(node.getType() == ShopUnitType.CATEGORY)
                    onCategoryOfferChanged(node.getParent(), node.getMetadata().getTotalPrice().negate(), node.getMetadata().getOffersCount().negate(), categoriesPool);
                saveCategoriesPool(categoriesPool, null);
            }
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    /**
     * Handles get requests.
     * @param uuid id of the unit to get
     * @throws UUIDFormatException if uuid string can't be converted to UUID
     * @throws ShopUnitNotFoundException if unit with such uuid was not found
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopUnitDto get(String uuid) {
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

    /**
     * Handles sales requests.
     * @param date a date
     * @throws WrongDateFormatException if date format does not match ISO 8601
     * @return a list of shop unit that were updated in last 24 hours from the date
     */
    @Transactional(rollbackFor = Exception.class)
    public StatisticResponse sales(String date) {
        LocalDateTime salesDate = ShopUnitMapper.getDate(date);
        return new StatisticResponse();
    }

    /**
     * Handles statistics requests.
     * @param id id of the unit to delete
     * @param start the beginning of the period
     * @param end end of the period
     * @throws WrongDateFormatException if date format does not match ISO 8601
     * @return a list of statistic (history of updates) during [start; end)
     */
    @Transactional(rollbackFor = Exception.class)
    public StatisticResponse statistics(String id, String start, String end) {
        try {
            UUID uuid = UUID.fromString(id);
            return new StatisticResponse();
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }


    /**
     * Inserts a subtree of shop units.
     * @param node the root of a subtree to insert
     * @param childrens adjacency list of the units hierarchy
     * @param categoriesPool a set of categories that will be affected during import
     * @param date date of update
     * @throws ShopUnitTypeChangeException if request tries to change the type of an existent unit
     * @throws WrongIdValueException if id is null
     * @throws WrongNameException if name is null
     * @throws WrongPriceValueException if price value is invalid
     * @throws UnknownUnitTypeException if unit type is unknown
     * @throws WrongParentDataException if id is equal to parent id or parent does not exists
     */
    @Async
    protected void insertNodeSubtree(ShopUnitImport node, Map<String, List<ShopUnitImport>> childrens, Set<ShopUnitEntity> categoriesPool, LocalDateTime date) {
        var storedOptional = unitRepository.findById(UUID.fromString(node.getId()));
        if(storedOptional.isPresent() && !storedOptional.get().getType().toString().equals(node.getType()))
            throw new UnknownUnitTypeException();

        ShopUnitEntity entity = storedOptional.orElseGet(ShopUnitEntity::new);

        entity.setId(UUID.fromString(node.getId()));
        entity.setName(node.getName());
        entity.setType(ShopUnitType.valueOf(node.getType()));
        entity.setUpdateDate(date);

        if(entity.getType() == ShopUnitType.CATEGORY) {
            // If the parent category has changed, we subtract the data of the imported category from its metadata
            if(entity.getParent() != null && !entity.getParent().getId().toString().equals(node.getParentId())) {
                onCategoryOfferChanged(
                        entity.getParent(),
                        entity.getMetadata().getTotalPrice().negate(),
                        entity.getMetadata().getOffersCount().negate(),
                        categoriesPool
                );
            }
            if(node.getParentId() != null) {
                var parentOptional = unitRepository.findById(UUID.fromString(node.getParentId()));
                if(parentOptional.isEmpty()) {
                    throw new WrongParentDataException();
                }
                entity.setParent(parentOptional.get());
                // todo test перемещения подкатегории.
            }
        }


        if(entity.getType() == ShopUnitType.OFFER) {
            Long priceDifference = storedOptional.map(e -> node.getPrice() - e.getPrice()).orElseGet(node::getPrice);
            boolean theSame = true;
            if(storedOptional.isPresent() && entity.getParent() != null && !entity.getParent().getId().toString().equals(node.getParentId())) {
                onCategoryOfferChanged(
                        entity.getParent(),
                        BigInteger.valueOf(-entity.getPrice()),
                        BigInteger.ONE.negate(),
                        categoriesPool
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
                onCategoryOfferChanged(
                        entity.getParent(),
                        BigInteger.valueOf(priceDifference),
                        theSame ? BigInteger.ZERO : BigInteger.ONE,
                        categoriesPool
                );
            }
        }

        unitRepository.save(entity);
        if(entity.getType() == ShopUnitType.OFFER) {
            var history = ShopUnitMapper.toHistoryEntity(entity, date);
            historyRepository.save(history);
        } else if(entity.getType() == ShopUnitType.CATEGORY) {
            categoriesPool.remove(entity);
            categoriesPool.add(entity);
        }

        var childsList = childrens.get(node.getId());
        for(ShopUnitImport child : childsList)
            insertNodeSubtree(child, childrens, categoriesPool, date);
    }

    /**
     * Deletes node and its subtree, BUT does not affect on parent category
     * @param node shop unit which subtree is being deleted
     */
    private void deleteNode(ShopUnitEntity node) {
        node.getChildrens().forEach(this::deleteNode);
        historyRepository.deleteAllByUnitId(node.getId());
        metaRepository.deleteAllByCategory(node);
        unitRepository.delete(node);
    }

    /**
     * Changes the total amount and number of values in the metadata when an offer in the subtree is changed.
     * @param category parent the of changed offer
     * @param priceChange changing the offer price
     * @param countChange total number of modified offers
     * @param categoriesPool  a set of categories that were affected during some process
     */
    private void onCategoryOfferChanged(ShopUnitEntity category, BigInteger priceChange, BigInteger countChange, Set<ShopUnitEntity> categoriesPool) {
        ShopCategoryMetaEntity meta;
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

        categoriesPool.remove(category);
        categoriesPool.add(category);

        if(category.getParent() != null)
            onCategoryOfferChanged(category.getParent(), priceChange, countChange, categoriesPool);
    }

    /**
     * Saves entities from categories pool
     * @param categoriesPool  a set of categories that were affected during some process
     * @param date the date that will be specified as the date of the change
     */
    private void saveCategoriesPool(Set<ShopUnitEntity> categoriesPool, LocalDateTime date) {
        for(ShopUnitEntity category : categoriesPool) {
            unitRepository.save(category);
            if(date != null) {
                category.setUpdateDate(date);
                var history = ShopUnitMapper.toHistoryEntity(category, date);
                historyRepository.save(history);
            }
        }
    }
}
