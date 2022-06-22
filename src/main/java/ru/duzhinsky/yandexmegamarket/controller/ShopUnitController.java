package ru.duzhinsky.yandexmegamarket.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import ru.duzhinsky.yandexmegamarket.dto.BadRequestDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.exceptions.*;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitDeleteService;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitGetService;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitImportsService;

@RestController
@RequestMapping("/")
@Log
public class ShopUnitController {
    @Autowired
    private ShopUnitImportsService importsService;

    @Autowired
    private ShopUnitDeleteService deleteService;

    @Autowired
    private ShopUnitGetService getService;

    @PostMapping("/imports")
    public WebAsyncTask<ResponseEntity> imports(@RequestBody ShopUnitImportRequestDto requestDto) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                importsService.importUnits(requestDto);
                return ResponseEntity.ok().build();
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new BadRequestDto(400,"Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }

    @DeleteMapping("/delete/{id}")
    public WebAsyncTask<ResponseEntity> delete(@PathVariable String id) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                deleteService.delete(id);
                return ResponseEntity.ok().build();
            }
            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.status(404).body(new BadRequestDto(404,"Item not found"));
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new BadRequestDto(400,"Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }

    @GetMapping("/nodes/{id}")
    public WebAsyncTask<ResponseEntity> get(@PathVariable String id) {
        log.info("Request /nodes/{id} with id = " + id);
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                return ResponseEntity.ok().body(getService.get(id));
            }
            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.status(404).body(new BadRequestDto(404,"Item not found"));
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new BadRequestDto(400, "Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }
}
