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
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportDto;
import ru.duzhinsky.yandexmegamarket.dto.ShopUnitImportRequestDto;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PostImportTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ShopUnitRepository unitRepository;

    private void isBadRequestResponseForInvalid(ResponseEntity<Object> response) {
        Assert.isTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST, "Status code should be 400");
        Assert.isTrue(response.getBody() != null, "Response should have body");
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>)response.getBody();
        Assert.isTrue("Validation Failed".equals(body.get("message")), "Message should be like \"Validation Failed\"");
        Assert.isTrue(Integer.valueOf(400).equals(body.get("code")), "Message code should be equal 400");
    }


    private ResponseEntity<Object> testInvalidRequest(ShopUnitImportRequestDto requestDto) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<ShopUnitImportRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange("/imports", HttpMethod.POST, requestEntity, Object.class, new HashMap<String, String>());
        isBadRequestResponseForInvalid(response);
        return response;
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

    @AfterEach
    public void clearDb() {
         unitRepository.deleteAll();
    }

    @Test
    public void shouldBeOkOnEmpty() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<ShopUnitImportDto> items = new ArrayList<>();
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );

        HttpEntity<ShopUnitImportRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange("/imports", HttpMethod.POST, requestEntity, Object.class, new HashMap<String, String>());

        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Status code should be OK - 200");
    }

    @Test
    public void shouldInsertRandom() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void uuidShouldPresent() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                null,
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceForOffersShouldNotBeNull() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                null
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceShouldNotBeNegative() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                -2L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceValids() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                0L
        ));
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void priceForCategoriesShouldBeNull() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                100L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceForCategoriesValid() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void forbidTypeChange() {
        UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
        List<ShopUnitImportDto> items1 = new ArrayList<>();
        items1.add(new ShopUnitImportDto(
                id1.toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        items1.add(new ShopUnitImportDto(
                id2.toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequestDto requestDto1 = new ShopUnitImportRequestDto(
                items1,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto1);

        List<ShopUnitImportDto> items2 = new ArrayList<>();
        items2.add(new ShopUnitImportDto(
                id1.toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        items2.add(new ShopUnitImportDto(
                id2.toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequestDto requestDto2 = new ShopUnitImportRequestDto(
                items2,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto2);
    }

    @Test
    public void shouldInsertRandomHierarchy() {
        UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID(), id3 = UUID.randomUUID();
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                id1.toString(),
                "off1",
                id3.toString(),
                ShopUnitType.OFFER.toString(),
                100L
        ));
        items.add(new ShopUnitImportDto(
                id2.toString(),
                "off2",
                id3.toString(),
                ShopUnitType.OFFER.toString(),
                100L
        ));
        items.add(new ShopUnitImportDto(
                id3.toString(),
                "cat",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void nameShouldNotBeNull() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        items.add(new ShopUnitImportDto(
                UUID.randomUUID().toString(),
                null,
                null,
                ShopUnitType.OFFER.toString(),
                150L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void noLoops() {
        List<ShopUnitImportDto> items = new ArrayList<>();
        UUID id = UUID.randomUUID();
        items.add(new ShopUnitImportDto(
                id.toString(),
                "some name",
                id.toString(),
                ShopUnitType.OFFER.toString(),
                150L
        ));
        ShopUnitImportRequestDto requestDto = new ShopUnitImportRequestDto(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void sampleAssertions() {
        List<ShopUnitImportDto> batch1 = new ArrayList<>();
        batch1.add(new ShopUnitImportDto(
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                "Товары",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequestDto requestDto;
        requestDto = new ShopUnitImportRequestDto(batch1, "2022-02-01T12:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        List<ShopUnitImportDto> batch2 = new ArrayList<>();
        batch2.add(new ShopUnitImportDto(
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "Смартфоны",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch2.add(new ShopUnitImportDto(
                "863e1a7a-1304-42ae-943b-179184c077e3",
                "jPhone 13",
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                ShopUnitType.OFFER.toString(),
                79999L
        ));
        batch2.add(new ShopUnitImportDto(
                "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                "Xomia readme 10",
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                ShopUnitType.OFFER.toString(),
                59999L
        ));
        requestDto = new ShopUnitImportRequestDto(batch2, "2022-02-02T12:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        List<ShopUnitImportDto> batch3 = new ArrayList<>();
        batch3.add(new ShopUnitImportDto(
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "Телевизоры",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch3.add(new ShopUnitImportDto(
                "98883e8f-0507-482f-bce2-2fb306cf6483",
                "Samson 70",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                32999L
        ));
        batch3.add(new ShopUnitImportDto(
                "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                "Phylis 10",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                49999L
        ));
        requestDto = new ShopUnitImportRequestDto(batch3, "2022-02-03T12:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        List<ShopUnitImportDto> batch4 = new ArrayList<>();
        batch4.add(new ShopUnitImportDto(
                "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                "Goldstart very smart",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                69999L
        ));
        requestDto = new ShopUnitImportRequestDto(batch4, "2022-02-03T15:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        var goodsOpt = unitRepository.findLatestVersion(UUID.fromString("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"));
        var tvOpt = unitRepository.findLatestVersion(UUID.fromString("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2"));
        var smartOpt = unitRepository.findLatestVersion(UUID.fromString("d515e43f-f3f6-4471-bb77-6b455017a2d2"));

        Assert.isTrue(goodsOpt.isPresent());
        Assert.isTrue(tvOpt.isPresent());
        Assert.isTrue(smartOpt.isPresent());
        Assert.isTrue(goodsOpt.get().getPrice().equals(58599L));
        Assert.isTrue(tvOpt.get().getPrice().equals(50999L));
        Assert.isTrue(smartOpt.get().getPrice().equals(69999L));

        List<ShopUnitImportDto> batch5 = new ArrayList<>();
        batch5.add(new ShopUnitImportDto(
                "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                "Goldstart very smart",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                99999L
        ));
        batch5.add(new ShopUnitImportDto(
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "Smartphones",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch5.add(new ShopUnitImportDto(
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "TV",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        requestDto = new ShopUnitImportRequestDto(batch5, "2022-02-04T15:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        goodsOpt = unitRepository.findLatestVersion(UUID.fromString("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"));
        tvOpt = unitRepository.findLatestVersion(UUID.fromString("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2"));
        smartOpt = unitRepository.findLatestVersion(UUID.fromString("d515e43f-f3f6-4471-bb77-6b455017a2d2"));

        Assert.isTrue(goodsOpt.isPresent());
        Assert.isTrue(tvOpt.isPresent());
        Assert.isTrue(smartOpt.isPresent());
        Assert.isTrue(goodsOpt.get().getPrice().equals(64599L));
        Assert.isTrue(tvOpt.get().getPrice().equals(60999L));
        Assert.isTrue(smartOpt.get().getPrice().equals(69999L));
    }
}
