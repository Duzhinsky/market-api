package ru.duzhinsky.yandexmegamarket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.Assert;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ShopUnitRepositoryTest {
    @Autowired
    private ShopUnitRepository shopUnitRepository;

    @Test
    public void shouldFindLastVerison() {
        ShopUnitEntity entity = new ShopUnitEntity();
        entity.setValidFrom(LocalDateTime.of(2000, 1, 1, 12, 30));
        entity.setValidTill(LocalDateTime.of(2000, 1, 10, 12, 30));
        entity.setUnitId(new UUID(2,1));
        entity.setName("some name");
        entity.setType(ShopUnitType.OFFER);
        entity = shopUnitRepository.save(entity);

        ShopUnitEntity entityChange = new ShopUnitEntity();
        entityChange.setUnitId(entity.getUnitId());
        entityChange.setValidFrom(LocalDateTime.of(2000, 1, 10, 12, 30));
        entityChange.setValidTill(null);
        entityChange.setUnitId(new UUID(2,1));
        entityChange.setName("some name");
        entityChange.setType(ShopUnitType.OFFER);
        shopUnitRepository.save(entityChange);

        Assert.isTrue(
                shopUnitRepository.findByUnitIdAndValidTillIsNull(entity.getUnitId())
                        .isPresent(),
                "The version should be found!"
        );

        Assert.isTrue(
                shopUnitRepository.findByUnitIdAndValidTillIsNull(entity.getUnitId())
                        .get().getId() == entityChange.getId(),
                "Should be found the latest version"
        );

        Assert.isTrue(
                shopUnitRepository.findAllByUnitId(entity.getUnitId()).size() == 2,
                "Unit should have 2 records"
        );
    }
}
