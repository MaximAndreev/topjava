package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;

public class UserMealWithExceed {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final boolean exceed;

    public UserMealWithExceed(UserMeal meal, boolean exceed) {
        this(meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceed);
    }

    public UserMealWithExceed(LocalDateTime dateTime, String description, int calories, boolean exceed) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.exceed = exceed;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Date: " + dateTime.toString() +
                " Desc: " + description +
                " Calories: " + calories +
                " Exceed: " + exceed;
    }
}
