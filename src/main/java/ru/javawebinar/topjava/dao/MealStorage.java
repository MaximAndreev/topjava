package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    Meal create(Meal meal);

    Meal read(Integer id);

    Meal update(Meal updatedMeal);

    boolean delete(Integer id);

    List<Meal> getAll();
}
