package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealStorageMap implements MealStorage {
    private final AtomicInteger counter = new AtomicInteger();
    private final Map<Integer, Meal> mealMap = new ConcurrentHashMap<>();

    @Override
    public Meal create(LocalDateTime dateTime, String description, int calories) {
        Meal meal = new Meal(counter.incrementAndGet(), dateTime, description, calories);
        mealMap.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal read(String id) {
        try {
            return mealMap.get(Integer.valueOf(id));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean update(Meal updatedMeal) {
        Integer id = updatedMeal.getId();
        if (mealMap.containsKey(id)) {
            mealMap.put(id, updatedMeal);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        try {
            Integer idInt = Integer.valueOf(id);
            if (mealMap.containsKey(idInt)) {
                mealMap.remove(idInt);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(mealMap.values());
    }
}
