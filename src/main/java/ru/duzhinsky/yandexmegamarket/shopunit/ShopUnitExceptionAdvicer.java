package ru.duzhinsky.yandexmegamarket.shopunit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.duzhinsky.yandexmegamarket.dto.ResponseMessage;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.BadRequestException;
import ru.duzhinsky.yandexmegamarket.shopunit.exception.ShopUnitNotFoundException;

@ControllerAdvice
public class ShopUnitExceptionAdvicer {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody ResponseMessage
    badRequest(BadRequestException ex) {
        return new ResponseMessage(400, "Validation Failed");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShopUnitNotFoundException.class)
    @ResponseBody ResponseMessage
    notFound(ShopUnitNotFoundException ex) {
        return new ResponseMessage(404, "Item not found");
    }
}
