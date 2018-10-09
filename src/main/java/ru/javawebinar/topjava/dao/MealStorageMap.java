package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealStorageMap implements MealStorage {
    private final AtomicInteger counter = new AtomicInteger();
    private final Map<Integer, Meal> mealMap = new ConcurrentHashMap<>();

    @Override
    public Meal create(Meal meal) {
        meal.setId(counter.incrementAndGet());
        mealMap.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal read(Integer id) {
        return mealMap.get(id);
    }

    @Override
    public Meal update(Meal updatedMeal) {
        Integer id = updatedMeal.getId();
        if (mealMap.containsKey(id)) {
            return mealMap.put(id, updatedMeal);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        return mealMap.remove(id) != null;
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(mealMap.values());
    }
}
