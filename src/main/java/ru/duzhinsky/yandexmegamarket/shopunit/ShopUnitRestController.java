package ru.duzhinsky.yandexmegamarket.shopunit;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitDto;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.StatisticResponse;

import java.util.logging.Level;

@RestController
@RequestMapping("/")
@Log
public class ShopUnitRestController {
    private final ShopUnitService unitService;

    @Autowired
    public ShopUnitRestController(ShopUnitService unitService) {
        this.unitService = unitService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/imports")
    public void imports(@RequestBody ShopUnitImportRequest requestDto) {
        log.info("Received /imports request");
        unitService.importUnits(requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        log.log(Level.INFO, "Received /delete/{0} request", id);
        unitService.delete(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/nodes/{id}")
    public ShopUnitDto get(@PathVariable String id) {
        log.log(Level.INFO, "Received /nodes/{0} request", id);
        return unitService.get(id);
    }

    @GetMapping("/sales")
    public StatisticResponse sales(@RequestParam String date) {
        log.log(Level.INFO, "Received /sales?date={0} request", date);
        return unitService.sales(date);
    }

    @GetMapping("/node/{id}/statistic")
    public StatisticResponse statistics(@PathVariable String id, @RequestParam String dateStart, @RequestParam String dateEnd) {
        log.log(Level.INFO, "Received /node/{0}/statistic?dateStart={1}&dateEnd={2} request", new Object[]{id, dateStart, dateEnd});
        return unitService.statistics(id, dateStart, dateEnd);
    }
}
