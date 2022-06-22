package ru.duzhinsky.yandexmegamarket.service.executors;

import ru.duzhinsky.yandexmegamarket.entity.ShopUnitEntity;
import ru.duzhinsky.yandexmegamarket.entity.ShopUnitType;
import ru.duzhinsky.yandexmegamarket.repository.ShopUnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class CategoryOffersTask extends RecursiveTask<List<ShopUnitEntity>> {

    private final ShopUnitEntity category;
    private final ShopUnitRepository unitRepository;

    public CategoryOffersTask(ShopUnitEntity category, ShopUnitRepository unitRepository) {
        this.category = category;
        this.unitRepository = unitRepository;
    }

    @Override
    public List<ShopUnitEntity> compute() {
        List<ShopUnitEntity> childs = unitRepository.findAllLatestByParent(category.getUnitId());
        List<ShopUnitEntity> result = new ArrayList<>();
        List<CategoryOffersTask> forkJoinTasks = new ArrayList<>();
        for(ShopUnitEntity child : childs) {
            if(child.getType() == ShopUnitType.OFFER) {
                result.add(child);
            } else if(child.getType() == ShopUnitType.CATEGORY) {
                var task = new CategoryOffersTask(child, unitRepository);
                forkJoinTasks.add(task);
            }
        }
        ForkJoinTask.invokeAll(forkJoinTasks);
        for(var task : forkJoinTasks) {
            var subtree = task.join();
            result.addAll(subtree);
        }
        return result;
    }
}
