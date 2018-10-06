package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealStorage {
    Meal create(LocalDateTime dateTime, String description, int calories);

    Meal read(String id);

    boolean update(Meal updatedMeal);

    boolean delete(String id);

    List<Meal> getAll();
}
