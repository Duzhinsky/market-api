package ru.duzhinsky.yandexmegamarket;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.duzhinsky.yandexmegamarket.dto.ResponseMessage;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.shopunit.ShopUnitService;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.BadRequestException;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.ShopUnitNotFoundException;

import java.util.logging.Level;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/")
@Log
public class RestController {
    @Autowired
    private ShopUnitService unitService;


    @PostMapping("/imports")
    public ResponseEntity imports(@RequestBody ShopUnitImportRequest requestDto) {
        log.info("Received /imports request");
        try {
            unitService.importUnits(requestDto);
            return ResponseEntity.ok().build();
        }
        catch (BadRequestException e) {
            log.warning(e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseMessage(400,"Validation Failed"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        log.log(Level.INFO, "Received /delete/{0} request", id);
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
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity get(@PathVariable String id) {
        log.log(Level.INFO, "Received /nodes/{0} request", id);
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
    }

    @GetMapping("/sales")
    public ResponseEntity sales(@RequestParam String date) {
        log.log(Level.INFO, "Received /sales?date={0} request", date);
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
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity statistics(@PathVariable String id, @RequestParam String dateStart, @RequestParam String dateEnd) {
        log.log(Level.INFO, "Received /node/{0}/statistic?dateStart={1}&dateEnd={2} request", new Object[]{id, dateStart, dateEnd});
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
    }
}
