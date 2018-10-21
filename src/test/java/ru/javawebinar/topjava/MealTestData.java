package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final Meal MEAL_1_USER = new Meal(START_SEQ + 2, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак / user", 500);
    public static final Meal MEAL_2_USER = new Meal(START_SEQ + 3, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед / user", 1000);
    public static final Meal MEAL_3_USER = new Meal(START_SEQ + 4, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин / user", 500);
    public static final Meal MEAL_4_USER = new Meal(START_SEQ + 5, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак / user", 1000);
    public static final Meal MEAL_5_USER = new Meal(START_SEQ + 6, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед / user", 500);
    public static final Meal MEAL_6_USER = new Meal(START_SEQ + 7, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин / user", 510);

    public static final Meal MEAL_1_ADMIN = new Meal(START_SEQ + 8, LocalDateTime.of(2018, Month.OCTOBER, 30, 10, 0), "Завтрак / admin", 500);
    public static final Meal MEAL_2_ADMIN = new Meal(START_SEQ + 9, LocalDateTime.of(2018, Month.OCTOBER, 30, 13, 0), "Обед / admin", 1000);
    public static final Meal MEAL_3_ADMIN = new Meal(START_SEQ + 10, LocalDateTime.of(2018, Month.OCTOBER, 30, 20, 0), "Ужин / admin", 500);
    public static final Meal MEAL_4_ADMIN = new Meal(START_SEQ + 11, LocalDateTime.of(2018, Month.OCTOBER, 31, 10, 0), "Завтрак / admin", 1000);
    public static final Meal MEAL_5_ADMIN = new Meal(START_SEQ + 12, LocalDateTime.of(2018, Month.OCTOBER, 31, 13, 0), "Обед / admin", 500);
    public static final Meal MEAL_6_ADMIN = new Meal(START_SEQ + 13, LocalDateTime.of(2018, Month.OCTOBER, 31, 20, 0), "Ужин / admin", 510);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(expected);
    }
}
