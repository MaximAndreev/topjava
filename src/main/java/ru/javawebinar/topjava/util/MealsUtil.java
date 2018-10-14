package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class MealsUtil {

    public static Meal mealForUser(Integer userId, LocalDateTime ldt, String description, int calories) {
        Meal meal = new Meal(ldt, description, calories);
        meal.setUserId(userId);
        return meal;
    }

    public static final List<Meal> MEALS = Arrays.asList(
            //User #1
            mealForUser(1, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак / user1", 500),
            mealForUser(1, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед / user1", 1000),
            mealForUser(1, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин / user1", 500),
            mealForUser(1, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак / user1", 1000),
            mealForUser(1, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед / user1", 500),
            mealForUser(1, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин / user1", 510),

            //User #2
            mealForUser(2, LocalDateTime.of(2016, Month.MAY, 30, 10, 0), "Завтрак / user2", 500),
            mealForUser(2, LocalDateTime.of(2016, Month.MAY, 30, 13, 0), "Обед / user2", 1000),
            mealForUser(2, LocalDateTime.of(2016, Month.MAY, 30, 20, 0), "Ужин / user2", 500),
            mealForUser(2, LocalDateTime.of(2016, Month.MAY, 31, 10, 0), "Завтрак / user2", 1000),
            mealForUser(2, LocalDateTime.of(2016, Month.MAY, 31, 13, 0), "Обед / user2", 500),
            mealForUser(2, LocalDateTime.of(2016, Month.MAY, 31, 20, 0), "Ужин / user2", 510)
    );
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static List<MealWithExceed> getWithExceeded(Collection<Meal> meals, int caloriesPerDay) {
        return getFilteredWithExceeded(meals, caloriesPerDay, meal -> true);
    }

    public static List<MealWithExceed> getFilteredWithExceeded(Collection<Meal> meals,
                                                               int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        return getFilteredWithExceeded(meals, caloriesPerDay, meal -> DateTimeUtil.isBetween(meal.getTime(), startTime, endTime));
    }

    private static List<MealWithExceed> getFilteredWithExceeded(Collection<Meal> meals,
                                                                int caloriesPerDay, Predicate<Meal> filter) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
//                      Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
                );

        return meals.stream()
                .filter(filter)
                .map(meal -> createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(toList());
    }

    public static MealWithExceed createWithExceed(Meal meal, boolean exceeded) {
        return new MealWithExceed(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceeded);
    }
}