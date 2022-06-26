package ru.duzhinsky.yandexmegamarket;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import ru.duzhinsky.yandexmegamarket.dto.ResponseMessage;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.shopunit.ShopUnitService;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.StatisticResponse;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.BadRequestException;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.ShopUnitNotFoundException;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/")
@Log
public class RestController {
    @Autowired
    private ShopUnitService unitService;


    @PostMapping("/imports")
    public WebAsyncTask<ResponseEntity> imports(@RequestBody ShopUnitImportRequest requestDto) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                unitService.importUnits(requestDto);
                return ResponseEntity.ok().build();
            }
            catch (BadRequestException e) {
                log.warning(e.getMessage());
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
            catch (BadRequestException e) {
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
            catch (BadRequestException e) {
                log.warning(e.getMessage());
                return ResponseEntity.badRequest().body(new ResponseMessage(400, "Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }

    @GetMapping("/sales")
    public WebAsyncTask<ResponseEntity> sales(@RequestParam String date) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                return ResponseEntity.ok().body(unitService.sales(date));
            }

            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.status(404).body(new ResponseMessage(404,"Item not found"));
            }
            catch (BadRequestException e) {
                log.warning(e.getMessage());
                return ResponseEntity.badRequest().body(new ResponseMessage(400, "Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }

    @GetMapping("/node/{id}/statistic")
    public WebAsyncTask<ResponseEntity> statistics(@PathVariable String id, @RequestParam String dateStart, @RequestParam String dateEnd) {
        WebAsyncTask<ResponseEntity> task = new WebAsyncTask<>(10000, () -> {
            try {
                return ResponseEntity.ok().body(unitService.statistics(id, dateStart, dateEnd));
            }

            catch (ShopUnitNotFoundException e) {
                return ResponseEntity.status(404).body(new ResponseMessage(404,"Item not found"));
            }
            catch (BadRequestException e) {
                log.warning(e.getMessage());
                return ResponseEntity.badRequest().body(new ResponseMessage(400, "Validation Failed"));
            }
        });
        task.onTimeout(() -> ResponseEntity.internalServerError().build());
        task.onError(() -> ResponseEntity.internalServerError().build());
        return task;
    }


}
