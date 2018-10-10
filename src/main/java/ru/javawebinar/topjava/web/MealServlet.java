package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.Config;
import ru.javawebinar.topjava.dao.MealStorage;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeParseException;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private final MealStorage mealStorage = Config.getStorage();

    @Override
    public void init() throws ServletException {
        super.init();
        mealStorage.create(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
        mealStorage.create(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000));
        mealStorage.create(new Meal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500));
        mealStorage.create(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000));
        mealStorage.create(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500));
        mealStorage.create(new Meal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String idString = request.getParameter("id");
        String caloriesString = request.getParameter("calories");
        String description = request.getParameter("description");
        String dateTimeString = request.getParameter("dateTimeString");

        int calories;
        LocalDateTime ldt;
        try {
            calories = Integer.parseInt(caloriesString);
            ldt = LocalDateTime.parse(dateTimeString);
        } catch (NullPointerException | NumberFormatException | DateTimeParseException e) {
            log.warn("bad meal - date: {}, calories: {}, description: {}",
                    dateTimeString,
                    caloriesString,
                    description);
            response.sendRedirect("meals");
            return;
        }
        Integer id = idStringToInteger(idString);
        if (id != null) {
            if (id == 0) {
                Meal meal = mealStorage.create(new Meal(ldt, description, calories));
                log.info("create meal with id: {}", meal.getId());
            } else {
                log.info("update meal with id: {}", id);
                if (mealStorage.update(new Meal(id, ldt, description, calories)) == null) {
                    log.warn("meal with id: {} not found", id);
                }
            }
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "add": {
                    log.debug("add new meal");
                    request.setAttribute("meal", new Meal(0, LocalDateTime.now().withSecond(0).withNano(0), "", 0));
                    request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                    return;
                }
                case "edit": {
                    String idSting = request.getParameter("id");
                    log.debug("edit meal with id: {}", idSting);
                    Integer id = idStringToInteger(idSting);
                    if (id == null) {
                        response.sendRedirect("meals");
                        return;
                    }
                    Meal meal = mealStorage.read(id);
                    if (meal == null) {
                        log.info("meal with id: {} not found", id);
                        response.sendRedirect("meals");
                    }
                    request.setAttribute("meal", meal);
                    request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                    return;
                }
                case "delete": {
                    String idSting = request.getParameter("id");
                    log.debug("delete meal with id: {}", idSting);
                    Integer id = idStringToInteger(idSting);
                    if (id == null) {
                        response.sendRedirect("meals");
                        return;
                    }
                    if (mealStorage.delete(id)) {
                        log.info("meal with id: {} not found", id);
                    }
                    response.sendRedirect("meals");
                    return;
                }
            }
        }
        log.debug("redirect to list of all meals");
        request.setAttribute("mealsWithExceed", MealsUtil.getFilteredWithExceeded(
                mealStorage.getAll(),
                LocalTime.MIN,
                LocalTime.MAX,
                2000));
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }

    private static Integer idStringToInteger(String idString) {
        try {
            return Integer.valueOf(idString);
        } catch (NumberFormatException e) {
            log.warn("bad meal id: {}", idString);
            return null;
        }
    }
}
