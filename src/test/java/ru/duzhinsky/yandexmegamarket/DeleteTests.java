package ru.duzhinsky.yandexmegamarket;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class DeleteTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ShopUnitRepository unitRepository;

    @AfterEach
    public void clearDb() {
        unitRepository.deleteAll();
    }

    private ResponseEntity<Object> testSuccessfullInsetion(ShopUnitImportRequestDto requestDto) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<ShopUnitImportRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange("/imports", HttpMethod.POST, requestEntity, Object.class, new HashMap<String, String>());
        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Status code should be OK - 200");
        for(ShopUnitImportDto ent : requestDto.getItems())
            Assert.isTrue(unitRepository.findLatestVersion(UUID.fromString(ent.getId())).isPresent(), "Entities should be created");
        return response;
    }

    private void isNotFoundResponse(ResponseEntity<Object> response) {
        Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Status code should be 404");
        Assert.isTrue(response.getBody() != null, "Response should have body");
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>)response.getBody();
        Assert.isTrue("Not found".equals(body.get("message")), "Message should be like \"Validation Failed\"");
        Assert.isTrue(Integer.valueOf(404).equals(body.get("code")), "Message code should be equal 404");
    }
}
