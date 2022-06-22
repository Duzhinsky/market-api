package ru.duzhinsky.yandexmegamarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import ru.duzhinsky.yandexmegamarket.dto.BadRequestDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.exceptions.*;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitDeleteService;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitImportsService;

@RestController
@RequestMapping("/")
public class ShopUnitController {
    @Autowired
    ShopUnitImportsService importsService;

    @Autowired
    ShopUnitDeleteService deleteService;

    @PostMapping("/imports")
    public WebAsyncTask<ResponseEntity> imports(@RequestBody ShopUnitImportRequestDto requestDto) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                importsService.importUnits(requestDto);
                return ResponseEntity.ok().build();
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new BadRequestDto("Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }

    @DeleteMapping("/delete/{id}")
    public WebAsyncTask<ResponseEntity> delete(@RequestParam String id) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                deleteService.delete(id);
                return ResponseEntity.ok().build();
            }
            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.badRequest().body(new BadRequestDto("Item not found"));
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new BadRequestDto("Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }
}
