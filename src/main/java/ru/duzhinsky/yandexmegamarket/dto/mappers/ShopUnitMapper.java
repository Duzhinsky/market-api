package ru.duzhinsky.yandexmegamarket.dto.mappers;

import org.springframework.stereotype.Component;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitDto;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImport;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitHistoryEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.exception.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShopUnitMapper {

    public static void validateImportDto(ShopUnitImport dto) {
        if(dto.getId() == null)
            throw new WrongIdValueException();
        if(dto.getName() == null)
            throw new WrongNameException();

        if("CATEGORY".equals(dto.getType())) {
            if(dto.getPrice() != null)
                throw new WrongPriceValueException();
        } else if("OFFER".equals(dto.getType())) {
            if(dto.getPrice() == null || dto.getPrice() < 0L)
                throw new WrongPriceValueException();
        } else {
            throw new UnknownUnitTypeException();
        }

        if(dto.getParentId() != null && dto.getParentId().equals(dto.getId()))
            throw new WrongParentDataException();
    }

    public ShopUnitDto toDto(ShopUnitEntity entity) {
        List<ShopUnitDto> childrens = null;
        if(entity.getType() == ShopUnitType.CATEGORY) {
            childrens = entity.getChildrens()
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        return new ShopUnitDto(
                entity.getId().toString(),
                entity.getName(),
                entity.getUpdateDate().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
                entity.getParent() == null ? null : entity.getParent().getId().toString(),
                entity.getType().toString(),
                entity.getPrice(),
                childrens
        );
    }

    public static ShopUnitHistoryEntity toHistoryEntity(ShopUnitEntity entity, LocalDateTime date) {
        ShopUnitHistoryEntity history = new ShopUnitHistoryEntity();
        history.setUnitId(entity.getId());
        history.setName(entity.getName());
        history.setType(entity.getType());
        history.setPrice(entity.getPrice());
        history.setUpdateDate(date);
        return history;
    }

    public static LocalDateTime getDateFromDto(ShopUnitImportRequest dto) {
        try {
            return LocalDateTime.parse(dto.getUpdateDate(), DateTimeFormatter.ISO_DATE_TIME);
        } catch(DateTimeParseException e) {
            throw new WrongDateFormatException("Date format does not matches ISO 8601");
        }
    }
}
