package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealWithExceed;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;

@Controller
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    @Autowired
    private MealService service;

    public List<MealWithExceed> getAll() {
        log.info("get all for user {}", authUserId());
        return MealsUtil.getWithExceeded(service.getAll(authUserId(), LocalDate.MIN, LocalDate.MAX),
                authUserCaloriesPerDay());
    }

    public List<MealWithExceed> getAll(LocalDate startDate,
                                       LocalDate endDate,
                                       LocalTime startTime,
                                       LocalTime endTime) {
        log.info("get all for user {} / startDate {}, endDate {}, startTime {}, endTime {}",
                authUserId(),
                startDate,
                endDate,
                startTime,
                endTime);
        return MealsUtil.getFilteredWithExceeded(
                service.getAll(authUserId(),
                        startDate != null ? startDate : LocalDate.MIN,
                        endDate != null ? endDate : LocalDate.MAX),
                authUserCaloriesPerDay(),
                startTime != null ? startTime : LocalTime.MIN,
                endTime != null ? endTime : LocalTime.MAX);
    }

    public List<Meal> getAllWithoutFilter() {
        log.info("get all without filter for user {}", authUserId());
        return service.getAll(authUserId(), LocalDate.MIN, LocalDate.MAX);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        meal.setUserId(authUserId());
        return service.create(meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        meal.setUserId(authUserId());
        service.update(meal);
    }
}
