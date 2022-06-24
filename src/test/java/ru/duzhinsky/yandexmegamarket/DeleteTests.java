package ru.duzhinsky.yandexmegamarket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImport;
import ru.duzhinsky.yandexmegamarket.dto.objects.ShopUnitImportRequest;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DeleteTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ShopUnitRepository unitRepository;

    @AfterEach
    public void clearDb() {
        unitRepository.deleteAll();
    }

    private ResponseEntity<Object> testSuccessfullInsetion(ShopUnitImportRequest requestDto) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<ShopUnitImportRequest> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange("/imports", HttpMethod.POST, requestEntity, Object.class, new HashMap<String, String>());
        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Status code should be OK - 200");
        for(ShopUnitImport ent : requestDto.getItems())
            Assert.isTrue(unitRepository.findById(UUID.fromString(ent.getId())).isPresent(), "Entities should be created");
        return response;
    }

    private void isNotFoundResponse(ResponseEntity<Object> response) {
        Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Status code should be 404");
        Assert.isTrue(response.getBody() != null, "Response should have body");
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>)response.getBody();
        Assert.isTrue("Item not found".equals(body.get("message")), "Message should be like \"Item not found\"");
        Assert.isTrue(Integer.valueOf(404).equals(body.get("code")), "Message code should be equal 404");
    }

    @Test
    public void askForNonExistent() {
        List<ShopUnitImport> items = new ArrayList<>();
        String id = UUID.randomUUID().toString();
        HttpEntity e = new HttpEntity(new LinkedHashMap<String, Object>());
        ResponseEntity<Object> response  = restTemplate.exchange("/delete/"+id, HttpMethod.DELETE, e, Object.class);
        isNotFoundResponse(response);
    }
}
