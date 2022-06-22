package ru.duzhinsky.yandexmegamarket.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
public class ShopUnitService {
    @Autowired
    private ShopUnitRepository unitRepository;

    private List<ShopUnitEntity> getAllCategoryOffers(ShopUnitEntity category) {
        List<ShopUnitEntity> childs = unitRepository.findAllByParent(category.getUnitId());
        List<ShopUnitEntity> result = new ArrayList<>();
        for(ShopUnitEntity child : childs) {
            if(child.getType() == ShopUnitType.OFFER) {
                result.add(child);
            } else if(child.getType() == ShopUnitType.CATEGORY) {
                List<ShopUnitEntity> subtree = getAllCategoryOffers(child);
                result.addAll(subtree);
            }
        }
        return result;
    }

    public Long calculateAveragePriceForCategory(ShopUnitEntity category) {
        List<ShopUnitEntity> offers = getAllCategoryOffers(category);
        var a = offers.stream().map(ShopUnitEntity::getPrice).map(l->l.toString()).collect(Collectors.joining(" "));
        log.info(category.getName() + ": " + a);
        if(offers.size() == 0) {
            return null;
        } else {
            BigInteger sum = BigInteger.ZERO;
            for(ShopUnitEntity offer : offers)
                sum = sum.add(BigInteger.valueOf(offer.getPrice()));
            return sum.divide(BigInteger.valueOf(offers.size())).longValue();
        }
    }
}
