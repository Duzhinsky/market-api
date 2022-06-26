package ru.duzhinsky.yandexmegamarket.shopunit.dto.mappers;

import org.springframework.stereotype.Component;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitDto;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImport;
import ru.duzhinsky.yandexmegamarket.shopunit.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.shopunit.entity.ShopUnitHistoryEntity;
import ru.duzhinsky.yandexmegamarket.shopunit.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for mapping entities to data transfer objects and dto to entities
 */
@Component
public class ShopUnitMapper {

    /**
     * Checks either the imported entity is valid.
     * @param dto DTO of imported entity
     * @throws WrongIdValueException if id is null
     * @throws WrongNameException if name is null
     * @throws WrongPriceValueException if price value is invalid
     * @throws UnknownUnitTypeException if unit type is unknown
     * @throws WrongParentDataException if id is equal to parent id
     */
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

    /**
     * Converts an entity to dto for get requests
     * @param entity an entity being converted
     * @return DTO of entity and it's sub hierarchy
     */
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

    /**
     * Converts a plain entity to new history entity.
     * Since history entity is a plain entity with a date, a plain entity and date are required.
     * @param entity entity being converted
     * @param date a date of change
     * @return A transient history entity
     */
    public static ShopUnitHistoryEntity toHistoryEntity(ShopUnitEntity entity, LocalDateTime date) {
        ShopUnitHistoryEntity history = new ShopUnitHistoryEntity();
        history.setUnitId(entity.getId());
        history.setName(entity.getName());
        history.setType(entity.getType());
        history.setPrice(entity.getPrice());
        history.setUpdateDate(date);
        return history;
    }

    /**
     * Retrieves a date from the import request dto
     * @param dto the request dto
     * @return a date from the request
     */
    public static LocalDateTime getDateFromDto(ShopUnitImportRequest dto) {
        try {
            return LocalDateTime.parse(dto.getUpdateDate(), DateTimeFormatter.ISO_DATE_TIME);
        } catch(DateTimeParseException e) {
            throw new WrongDateFormatException("Date format does not matches ISO 8601");
        }
    }
}
