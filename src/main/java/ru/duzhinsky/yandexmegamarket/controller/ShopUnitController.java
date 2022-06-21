package ru.duzhinsky.yandexmegamarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;
import ru.duzhinsky.yandexmegamarket.dto.BadRequestDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.exceptions.ShopUnitTypeChangeException;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongParentDataException;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongDateFormatException;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongPriceValueException;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitImportsService;

@RestController
@RequestMapping("/")
public class ShopUnitController {
    @Autowired
    ShopUnitImportsService importsService;

    @PostMapping("/imports")
    public WebAsyncTask<ResponseEntity> imports(@RequestBody ShopUnitImportRequestDto requestDto) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                importsService.importUnits(requestDto);
                return ResponseEntity.ok().build();
            }
            catch ( WrongDateFormatException |
                    WrongParentDataException |
                    WrongPriceValueException |
                    ShopUnitTypeChangeException e) {
                return ResponseEntity.badRequest().body(new BadRequestDto("Validation Failed"));
            }
            catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }
}
