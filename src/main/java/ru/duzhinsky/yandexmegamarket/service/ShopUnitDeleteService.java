package ru.duzhinsky.yandexmegamarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duzhinsky.yandexmegamarket.exceptions.ShopUnitNotFoundException;
import ru.duzhinsky.yandexmegamarket.exceptions.UUIDFormatException;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.util.UUID;

@Service
public class ShopUnitDeleteService {

    @Autowired
    private ShopUnitRepository unitRepository;

    @Autowired
    private ShopUnitService shopUnitService;

    @Transactional(rollbackFor = Exception.class)
    public void delete(String uuid)
            throws UUIDFormatException,
            ShopUnitNotFoundException
    {
        try {
            UUID id = UUID.fromString(uuid);
            var last = unitRepository.findLatestVersion(id);
            if(last.isEmpty())
                throw new ShopUnitNotFoundException();
            var parentUUID = last.get().getParent();
            deleteNode(id);
            if(parentUUID != null) {
                var parentOpt = unitRepository.findLatestVersion(parentUUID);
                if(parentOpt.isPresent()) {
                    var parent = parentOpt.get();
                    Long price = shopUnitService.calculateAveragePriceForCategory(parent);
                    parent.setPrice(price);
                    unitRepository.save(parent);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }

    public void deleteNode(UUID id) {
        var self = unitRepository.findAllByUnitId(id);
        var childrens = unitRepository.findAllByParent(id);
        for(var child : childrens)
            deleteNode(child.getUnitId());
        unitRepository.deleteAll(self);
    }
}
