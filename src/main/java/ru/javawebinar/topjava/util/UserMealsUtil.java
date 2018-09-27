package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 14, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        System.out.println("Result for loops:");
        System.out.println(getFilteredWithExceeded(mealList,
                LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000));
        System.out.println("Result for stream:");
        System.out.println(getFilteredWithExceededStream(mealList,
                LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList,
                                                                   LocalTime startTime,
                                                                   LocalTime endTime,
                                                                   int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesMap = new HashMap<>();
        for (UserMeal meal : mealList) {
            caloriesMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExceed> exceededMealList = new ArrayList<>();
        for (UserMeal meal : mealList) {
            LocalDateTime mealDateTime = meal.getDateTime();
            if (TimeUtil.isBetween(mealDateTime.toLocalTime(), startTime, endTime)) {
                exceededMealList.add(new UserMealWithExceed(mealDateTime,
                        meal.getDescription(),
                        meal.getCalories(),
                        caloriesMap.get(mealDateTime.toLocalDate()) > caloriesPerDay));
            }
        }
        return exceededMealList;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStream(List<UserMeal> mealList,
                                                                         LocalTime startTime,
                                                                         LocalTime endTime,
                                                                         int caloriesPerDay) {
        return mealList
                .stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .entrySet()
                .stream()
                .flatMap(e -> {
                    int caloriesSum = e.getValue().stream().map(UserMeal::getCalories).reduce(0, Integer::sum);
                    return e.getValue().stream()
                            .filter(meal -> TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExceed(meal.getDateTime(),
                                    meal.getDescription(),
                                    meal.getCalories(),
                                    caloriesSum > caloriesPerDay));
                })
                .collect(Collectors.toList());
    }
}
