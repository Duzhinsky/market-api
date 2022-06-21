package ru.duzhinsky.yandexmegamarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.exceptions.WrongDateFormatException;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@Service
public class ShopUnitImportsService {

    @Transactional
    public void importUnits(ShopUnitImportRequestDto requestDto) throws WrongDateFormatException {
        Date importDate = getDateFromDto(requestDto);
    }

    private static Date getDateFromDto(ShopUnitImportRequestDto dto) throws WrongDateFormatException {
        try {
            TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(dto.getDate());
            Instant i = Instant.from(ta);
            return Date.from(i);
        } catch(DateTimeParseException e) {
            throw new WrongDateFormatException("Date format does not fits iso 8601");
        }
    }
}













