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
import java.time.format.DateTimeParseException;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final MealStorage mealStorage = Config.getStorage();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
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

        if (id.isEmpty()) {
            Meal meal = mealStorage.create(ldt, description, calories);
            log.info("create meal with id: {}", meal.getId());
        } else {
            try {
                Meal updatedMeal = new Meal(Integer.valueOf(id), ldt, description, calories);
                if (!mealStorage.update(updatedMeal)) {
                    log.warn("meal with id: {} not found", id);
                }
            } catch (NumberFormatException e) {
                log.warn("bad meal id: {}", id);
            }
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            log.debug("redirect to list of all meals");
            request.setAttribute("mealsWithExceed", MealsUtil.getFilteredWithExceeded(
                    mealStorage.getAll(),
                    LocalTime.MIN,
                    LocalTime.MAX,
                    2000));
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
            return;
        }
        switch (action) {
            case "add": {
                log.debug("add new meal");
                request.setAttribute("meal", null);
                request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                return;
            }
            case "edit": {
                String id = request.getParameter("id");
                log.debug("update meal with id: {}", id);
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
                String id = request.getParameter("id");
                log.debug("remove meal with id: {}", id);
                if (!mealStorage.delete(id)) {
                    log.info("meal with id: {} not found", id);
                }
                response.sendRedirect("meals");
                return;
            }
            default: {
                log.info("invalid action");
                response.sendRedirect("meals");
            }
        }
    }
}
