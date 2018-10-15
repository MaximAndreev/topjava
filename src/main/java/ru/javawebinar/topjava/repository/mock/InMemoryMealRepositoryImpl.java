package ru.javawebinar.topjava.repository.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepositoryImpl.class);
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(meal.getUserId(), ConcurrentHashMap::new);
            repository.get(meal.getUserId()).put(meal.getId(), meal);
            return meal;
        }
        // update must check for consistency of userId
        Meal oldMeal = repository.getOrDefault(meal.getUserId(), Collections.emptyMap()).get(meal.getId());
        return oldMeal != null ? repository.get(meal.getUserId()).put(meal.getId(), meal) : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete {}", id);
        if (get(id, userId) != null) {
            Meal removedValue = repository.get(userId).remove(id);
            if (repository.get(userId).size() == 0) {
                repository.remove(userId);
            }
            return removedValue != null;
        }
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get {}", id);
        return repository.getOrDefault(userId, Collections.emptyMap()).get(id);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate start, LocalDate end) {
        log.info("getAll for user {}, start {}, end {}", userId, start, end);
        return getAll(userId)
                .stream()
                .filter(m -> DateTimeUtil.isBetween(m.getDate(), start, end))
                .sorted((m1, m2) -> -m1.getDateTime().compareTo(m2.getDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll for user {}", userId);
        return new ArrayList<>(repository.getOrDefault(userId, Collections.emptyMap()).values());
    }
}
