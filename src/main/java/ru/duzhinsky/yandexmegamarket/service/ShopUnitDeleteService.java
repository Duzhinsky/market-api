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

    @Transactional(rollbackFor = Exception.class)
    public void delete(String uuid)
            throws UUIDFormatException,
            ShopUnitNotFoundException
    {
        try {
            UUID id = UUID.fromString(uuid);
            if(unitRepository.findAllByUnitId(id).isEmpty())
                throw new ShopUnitNotFoundException();
            unitRepository.deleteAllByUnitId(id);
        } catch (IllegalArgumentException e) {
            throw new UUIDFormatException();
        }
    }
}
