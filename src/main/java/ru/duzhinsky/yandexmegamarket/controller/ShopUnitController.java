package ru.duzhinsky.yandexmegamarket.controller;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import ru.duzhinsky.yandexmegamarket.dto.objects.ResponseMessage;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.exception.*;
import ru.duzhinsky.yandexmegamarket.service.ShopUnitService;

@RestController
@RequestMapping("/")
@Log
public class ShopUnitController {
    @Autowired
    private ShopUnitService unitService;


    @PostMapping("/imports")
    public WebAsyncTask<ResponseEntity> imports(@RequestBody ShopUnitImportRequest requestDto) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                unitService.importUnits(requestDto);
                return ResponseEntity.ok().build();
            }
            catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new ResponseMessage(400,"Validation Failed"));
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
                unitService.delete(id);
                return ResponseEntity.ok().build();
            }
            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.status(404).body(new ResponseMessage(404,"Item not found"));
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new ResponseMessage(400,"Validation Failed"));
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
                return ResponseEntity.ok().body(unitService.get(id));
            }
            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.status(404).body(new ResponseMessage(404,"Item not found"));
            }
            catch (Exception e) {
                return ResponseEntity.badRequest().body(new ResponseMessage(400, "Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }
}
