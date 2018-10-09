package ru.javawebinar.topjava;

import ru.javawebinar.topjava.dao.MealStorage;
import ru.javawebinar.topjava.dao.MealStorageMap;

public class Config {
    private static final MealStorage mealStorage = new MealStorageMap();

    public static MealStorage getStorage() {
        return mealStorage;
    }
}
