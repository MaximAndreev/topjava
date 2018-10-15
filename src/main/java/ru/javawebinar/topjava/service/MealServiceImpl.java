package ru.javawebinar.topjava.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealServiceImpl implements MealService {
    private static final Logger log = LoggerFactory.getLogger(MealServiceImpl.class);
    private MealRepository repository;

    @Autowired
    public MealServiceImpl(MealRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meal create(Meal meal) {
        log.info("save {}", meal);
        return repository.save(meal);
    }

    @Override
    public void delete(int id, int userId) throws NotFoundException {
        log.info("delete id {}, userId {}", id, userId);
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    @Override
    public Meal get(int id, int userId) throws NotFoundException {
        log.info("get id {}, userId {}", id, userId);
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    @Override
    public void update(Meal meal) {
        log.info("update {}", meal);
        checkNotFoundWithId(repository.save(meal), meal.getId());
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll for userId {}", userId);
        return repository.getAll(userId);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate start, LocalDate end) {
        log.info("getAll for user {}, start {}, end {}", userId, start, end);
        return repository.getAllFiltered(userId, start, end);
    }
}
