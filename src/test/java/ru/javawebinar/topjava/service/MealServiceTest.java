package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        assertMatch(service.get(100002, USER_ID), MEAL_1_USER);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        service.get(1, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getSomeoneElsesFood() {
        service.get(MEAL_5_ADMIN.getId(), USER_ID);
    }

    @Test
    public void delete() {
        service.delete(100003, USER_ID);
        assertMatch(service.getAll(USER_ID), MEAL_6_USER, MEAL_5_USER, MEAL_4_USER, MEAL_3_USER, MEAL_1_USER);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() {
        service.delete(1, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteSomeoneElsesFood() {
        service.delete(MEAL_5_ADMIN.getId(), USER_ID);
    }

    @Test
    public void getBetweenDates() {
        List<Meal> betweenDates = service.getBetweenDates(LocalDate.of(2018, Month.OCTOBER, 30),
                LocalDate.of(2018, Month.OCTOBER, 30), ADMIN_ID);
        assertMatch(betweenDates, MEAL_3_ADMIN, MEAL_2_ADMIN, MEAL_1_ADMIN);
    }

    @Test
    public void getBetweenDateTimes() {
        List<Meal> betweenDateTimes = service.getBetweenDateTimes(LocalDateTime.of(2015, Month.MAY, 30, 10, 0),
                LocalDateTime.of(2015, Month.MAY, 30, 14, 0), USER_ID);
        assertMatch(betweenDateTimes, MEAL_2_USER, MEAL_1_USER);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), MEAL_6_USER, MEAL_5_USER, MEAL_4_USER, MEAL_3_USER, MEAL_2_USER, MEAL_1_USER);
    }

    @Test
    public void update() {
        Meal copyMeal = new Meal(MEAL_5_ADMIN);
        copyMeal.setCalories(1);
        copyMeal.setDescription("copy meal");
        service.update(copyMeal, ADMIN_ID);
        assertMatch(service.get(copyMeal.getId(), ADMIN_ID), copyMeal);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        Meal copyMeal = new Meal(MEAL_4_ADMIN);
        copyMeal.setId(1);
        service.update(copyMeal, ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void updateSomeoneElsesFood() {
        service.update(MEAL_6_ADMIN, USER_ID);
    }

    @Test
    public void create() {
        Meal meal = new Meal(LocalDateTime.of(2233, Month.MAY, 30, 12, 0), "завтра будущего", 9000);
        Meal createdMeal = service.create(meal, USER_ID);
        meal.setId(createdMeal.getId());
        assertMatch(createdMeal, meal);
    }
}
