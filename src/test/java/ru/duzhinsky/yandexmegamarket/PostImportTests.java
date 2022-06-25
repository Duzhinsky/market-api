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
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.repository.ShopCategoryMetaRepository;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitHistoryRepository;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PostImportTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ShopUnitRepository unitRepository;
    @Autowired
    private ShopUnitHistoryRepository historyRepository;
    @Autowired
    private ShopCategoryMetaRepository metaRepository;

    private void isBadRequestResponseForInvalid(ResponseEntity<Object> response) {
        Assert.isTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST, "Status code should be 400");
        Assert.isTrue(response.getBody() != null, "Response should have body");
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>)response.getBody();
        Assert.isTrue("Validation Failed".equals(body.get("message")), "Message should be like \"Validation Failed\"");
        Assert.isTrue(Integer.valueOf(400).equals(body.get("code")), "Message code should be equal 400");
    }


    private ResponseEntity<Object> testInvalidRequest(ShopUnitImportRequest requestDto) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<ShopUnitImportRequest> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange("/imports", HttpMethod.POST, requestEntity, Object.class, new HashMap<String, String>());
        isBadRequestResponseForInvalid(response);
        return response;
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

    @AfterEach
    public void clearDb() {
        historyRepository.deleteAll();
        metaRepository.deleteAll();
        unitRepository.deleteAll();
    }

    @Test
    public void shouldBeOkOnEmpty() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<ShopUnitImport> items = new ArrayList<>();
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );

        HttpEntity<ShopUnitImportRequest> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange("/imports", HttpMethod.POST, requestEntity, Object.class, new HashMap<String, String>());

        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Status code should be OK - 200");
    }

    @Test
    public void shouldInsertRandom() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void uuidShouldPresent() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                null,
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceForOffersShouldNotBeNull() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                null
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceShouldNotBeNegative() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                -2L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceValids() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                0L
        ));
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void priceForCategoriesShouldBeNull() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                100L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void priceForCategoriesValid() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void forbidTypeChange() {
        UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID();
        List<ShopUnitImport> items1 = new ArrayList<>();
        items1.add(new ShopUnitImport(
                id1.toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        items1.add(new ShopUnitImport(
                id2.toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        ShopUnitImportRequest requestDto1 = new ShopUnitImportRequest(
                items1,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto1);

        List<ShopUnitImport> items2 = new ArrayList<>();
        items2.add(new ShopUnitImport(
                id1.toString(),
                "random",
                null,
                ShopUnitType.OFFER.toString(),
                100L
        ));
        items2.add(new ShopUnitImport(
                id2.toString(),
                "random",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequest requestDto2 = new ShopUnitImportRequest(
                items2,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto2);
    }

    @Test
    public void shouldInsertRandomHierarchy() {
        UUID id1 = UUID.randomUUID(), id2 = UUID.randomUUID(), id3 = UUID.randomUUID();
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                id1.toString(),
                "off1",
                id3.toString(),
                ShopUnitType.OFFER.toString(),
                100L
        ));
        items.add(new ShopUnitImport(
                id2.toString(),
                "off2",
                id3.toString(),
                ShopUnitType.OFFER.toString(),
                100L
        ));
        items.add(new ShopUnitImport(
                id3.toString(),
                "cat",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testSuccessfullInsetion(requestDto);
    }

    @Test
    public void nameShouldNotBeNull() {
        List<ShopUnitImport> items = new ArrayList<>();
        items.add(new ShopUnitImport(
                UUID.randomUUID().toString(),
                null,
                null,
                ShopUnitType.OFFER.toString(),
                150L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void noLoops() {
        List<ShopUnitImport> items = new ArrayList<>();
        UUID id = UUID.randomUUID();
        items.add(new ShopUnitImport(
                id.toString(),
                "some name",
                id.toString(),
                ShopUnitType.OFFER.toString(),
                150L
        ));
        ShopUnitImportRequest requestDto = new ShopUnitImportRequest(
                items,
                "2022-02-02T12:00:00.000Z"
        );
        testInvalidRequest(requestDto);
    }

    @Test
    public void sampleAssertions() {
        List<ShopUnitImport> batch1 = new ArrayList<>();
        batch1.add(new ShopUnitImport(
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                "Товары",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        ShopUnitImportRequest requestDto;
        requestDto = new ShopUnitImportRequest(batch1, "2022-02-01T12:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        List<ShopUnitImport> batch2 = new ArrayList<>();
        batch2.add(new ShopUnitImport(
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "Смартфоны",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch2.add(new ShopUnitImport(
                "863e1a7a-1304-42ae-943b-179184c077e3",
                "jPhone 13",
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                ShopUnitType.OFFER.toString(),
                79999L
        ));
        batch2.add(new ShopUnitImport(
                "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                "Xomia readme 10",
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                ShopUnitType.OFFER.toString(),
                59999L
        ));
        requestDto = new ShopUnitImportRequest(batch2, "2022-02-02T12:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        List<ShopUnitImport> batch3 = new ArrayList<>();
        batch3.add(new ShopUnitImport(
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "Телевизоры",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch3.add(new ShopUnitImport(
                "98883e8f-0507-482f-bce2-2fb306cf6483",
                "Samson 70",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                32999L
        ));
        batch3.add(new ShopUnitImport(
                "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                "Phylis 10",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                49999L
        ));
        requestDto = new ShopUnitImportRequest(batch3, "2022-02-03T12:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        List<ShopUnitImport> batch4 = new ArrayList<>();
        batch4.add(new ShopUnitImport(
                "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                "Goldstart very smart",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                69999L
        ));
        requestDto = new ShopUnitImportRequest(batch4, "2022-02-03T15:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        var goodsOpt = unitRepository.findById(UUID.fromString("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"));
        var tvOpt = unitRepository.findById((UUID.fromString("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2")));
        var smartOpt = unitRepository.findById((UUID.fromString("d515e43f-f3f6-4471-bb77-6b455017a2d2")));

        Assert.isTrue(goodsOpt.isPresent());
        Assert.isTrue(tvOpt.isPresent());
        Assert.isTrue(smartOpt.isPresent());
        Assert.isTrue(goodsOpt.get().getPrice().equals(58599L));
        Assert.isTrue(tvOpt.get().getPrice().equals(50999L));
        Assert.isTrue(smartOpt.get().getPrice().equals(69999L));

        List<ShopUnitImport> batch5 = new ArrayList<>();
        batch5.add(new ShopUnitImport(
                "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                "Goldstart very smart",
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                ShopUnitType.OFFER.toString(),
                99999L
        ));
        batch5.add(new ShopUnitImport(
                "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                "Smartphones",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch5.add(new ShopUnitImport(
                "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                "TV",
                "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        requestDto = new ShopUnitImportRequest(batch5, "2022-02-04T15:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        goodsOpt = unitRepository.findById((UUID.fromString("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1")));
        tvOpt = unitRepository.findById((UUID.fromString("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2")));
        smartOpt = unitRepository.findById((UUID.fromString("d515e43f-f3f6-4471-bb77-6b455017a2d2")));

        Assert.isTrue(goodsOpt.isPresent());
        Assert.isTrue(tvOpt.isPresent());
        Assert.isTrue(smartOpt.isPresent());
        Assert.isTrue(goodsOpt.get().getPrice().equals(64599L));
        Assert.isTrue(tvOpt.get().getPrice().equals(60999L));
        Assert.isTrue(smartOpt.get().getPrice().equals(69999L));
    }

    @Test
    public void shift() {
        List<ShopUnitImport> batch1 = new ArrayList<>();
        batch1.add(new ShopUnitImport(
                "e0434784-f284-11ec-b939-0242ac120002",
                "root",
                null,
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch1.add(new ShopUnitImport(
                "1e1079ba-f285-11ec-b939-0242ac120002",
                "Cat 1",
                "e0434784-f284-11ec-b939-0242ac120002",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch1.add(new ShopUnitImport(
                "38a129e6-f285-11ec-b939-0242ac120002",
                "Cat 2",
                "e0434784-f284-11ec-b939-0242ac120002",
                ShopUnitType.CATEGORY.toString(),
                null
        ));
        batch1.add(new ShopUnitImport(
                "69bd992e-f285-11ec-b939-0242ac120002",
                "Off 1",
                "1e1079ba-f285-11ec-b939-0242ac120002",
                ShopUnitType.OFFER.toString(),
                100L
        ));
        batch1.add(new ShopUnitImport(
                "59149b86-f285-11ec-b939-0242ac120002",
                "Off 2",
                "38a129e6-f285-11ec-b939-0242ac120002",
                ShopUnitType.OFFER.toString(),
                400L
        ));
        var requestDto = new ShopUnitImportRequest(batch1, "2022-02-04T15:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        var rootOpt = unitRepository.findById((UUID.fromString("e0434784-f284-11ec-b939-0242ac120002")));
        Assert.isTrue(rootOpt.isPresent(), "Root should present");
        Assert.isTrue(rootOpt.get().getPrice().equals(250L), "Av price is 250");

        var cat1Opt = unitRepository.findById((UUID.fromString("1e1079ba-f285-11ec-b939-0242ac120002")));
        Assert.isTrue(cat1Opt.isPresent(), "Category 1 should present");
        Assert.isTrue(cat1Opt.get().getPrice().equals(100L), "Category 1 price should be 100");

        List<ShopUnitImport> batch2 = new ArrayList<>();
        batch2.add(new ShopUnitImport(
                "69bd992e-f285-11ec-b939-0242ac120002",
                "Off 1",
                "38a129e6-f285-11ec-b939-0242ac120002",
                ShopUnitType.OFFER.toString(),
                100L
        ));
        batch2.add(new ShopUnitImport(
                "59149b86-f285-11ec-b939-0242ac120002",
                "Off 2",
                "1e1079ba-f285-11ec-b939-0242ac120002",
                ShopUnitType.OFFER.toString(),
                400L
        ));
        requestDto = new ShopUnitImportRequest(batch2, "2022-02-05T15:00:00.000Z");
        testSuccessfullInsetion(requestDto);

        cat1Opt = unitRepository.findById((UUID.fromString("1e1079ba-f285-11ec-b939-0242ac120002")));
        Assert.isTrue(cat1Opt.isPresent(), "Category 1 should present");
        Assert.isTrue(cat1Opt.get().getPrice().equals(400L), "Category 1 av price should change");
    }
}
