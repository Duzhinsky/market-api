package ru.duzhinsky.yandexmegamarket;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImport;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.exception.*;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitService;

import java.util.ArrayList;
import java.util.List;

@Component
@Log
public class ApplicationWarmup {
    @Autowired
    private ShopUnitService unitService;

    public void warmup() {
        log.info("The Application is warming up...");
        List<ShopUnitImport> batch1 = new ArrayList<>();
        batch1.add(new ShopUnitImport(
                "e0434784-f284-11ec-b939-0242ac120002",
                "root",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch1.add(new ShopUnitImport(
                "1e1079ba-f285-11ec-b939-0242ac120002",
                "Cat 1",
                "e0434784-f284-11ec-b939-0242ac120002",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch1.add(new ShopUnitImport(
                "38a129e6-f285-11ec-b939-0242ac120002",
                "Cat 2",
                "e0434784-f284-11ec-b939-0242ac120002",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch1.add(new ShopUnitImport(
                "69bd992e-f285-11ec-b939-0242ac120002",
                "Off 1",
                "1e1079ba-f285-11ec-b939-0242ac120002",
                ShopUnitType.OFFER.toString(),
                100L
        ));
        batch1.add(new ShopUnitImport(
                "59149b86-f285-11ec-b939-0242ac120002",
                "Off 2",
                "38a129e6-f285-11ec-b939-0242ac120002",
                ShopUnitType.OFFER.toString(),
                400L
        ));
        var requestDto = new ShopUnitImportRequest(batch1, "2022-02-04T15:00:00.000Z");
        try {
            unitService.importUnits(requestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unitService.get("e0434784-f284-11ec-b939-0242ac120002");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unitService.delete("e0434784-f284-11ec-b939-0242ac120002");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Warming up has been completed");
    }
}
