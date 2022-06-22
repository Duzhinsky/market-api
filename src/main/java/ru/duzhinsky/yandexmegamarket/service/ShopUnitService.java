package ru.duzhinsky.yandexmegamarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;
import ru.duzhinsky.yandexmegamarket.service.executors.CategoryOffersTask;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
public class ShopUnitService {
    @Autowired
    private ShopUnitRepository unitRepository;

    private List<ShopUnitEntity> getAllCategoryOffers(ShopUnitEntity category) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        return forkJoinPool.invoke(new CategoryOffersTask(category, unitRepository));
    }

    public Long calculateAveragePriceForCategory(ShopUnitEntity category) {
        List<ShopUnitEntity> offers = getAllCategoryOffers(category);
        if(offers.size() == 0) {
            return null;
        } else {
            BigInteger sum = BigInteger.ZERO;
            for(ShopUnitEntity offer : offers) {
                sum = sum.add(BigInteger.valueOf(offer.getPrice()));
            }
            return sum.divide(BigInteger.valueOf(offers.size())).longValue();
        }
    }
}
