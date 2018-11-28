package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.converters.StringToLocalDate;
import ru.javawebinar.topjava.web.converters.StringToLocalTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.*;
import static ru.javawebinar.topjava.web.json.JsonUtil.writeValue;


class MealRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    protected MealService mealService;

    @Test
    void testGet() throws Exception {
        mockMvc.perform(get(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(MEAL1));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(mealService.getAll(SecurityUtil.authUserId()), MEAL6, MEAL5, MEAL4, MEAL3, MEAL2);
    }

    @Test
    void testCreateWithLocation() throws Exception {
        Meal expected = getCreated();
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andExpect(status().isCreated());

        Meal returned = readFromJson(action, Meal.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(mealService.getAll(SecurityUtil.authUserId()), expected, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    void testUpdate() throws Exception {
        Meal expected = getUpdated();
        mockMvc.perform(put(REST_URL + expected.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andExpect(status().isNoContent());
        assertMatch(mealService.get(expected.getId(), SecurityUtil.authUserId()), expected);
    }

    @Test
    void testGetAll() throws Exception {
        String actual = getContent(mockMvc.perform(get(REST_URL)));
        String expected = writeValue(List.of(
                MealsUtil.createWithExcess(MEAL6, true),
                MealsUtil.createWithExcess(MEAL5, true),
                MealsUtil.createWithExcess(MEAL4, true),
                MealsUtil.createWithExcess(MEAL3, false),
                MealsUtil.createWithExcess(MEAL2, false),
                MealsUtil.createWithExcess(MEAL1, false)));
        assertEquals(expected, actual);
    }

    @Test
    void testGetBetweenAllFullParams() throws Exception {
        LocalDateTime start = LocalDateTime.of(2015, 5, 30, 20, 0);
        LocalDateTime end = LocalDateTime.of(2015, 6, 30, 20, 0);
        String actual = getContent(mockMvc.perform(get(REST_URL + "?" + datesToParams(
                start.toLocalDate(),
                start.toLocalTime(),
                end.toLocalDate(),
                end.toLocalTime()
        ))));
        String expected = writeValue(List.of(
                MealsUtil.createWithExcess(MEAL6, true),
                MealsUtil.createWithExcess(MEAL3, false)));
        assertEquals(expected, actual);
    }

    @Test
    void testGetBetweenAllOnlyDate() throws Exception {
        String actual = getContent(mockMvc.perform(get(REST_URL + "?" + datesToParams(
                LocalDate.of(2015, 5, 30),
                null,
                LocalDate.of(2015, 5, 30),
                null
        ))));
        String expected = writeValue(List.of(
                MealsUtil.createWithExcess(MEAL3, false),
                MealsUtil.createWithExcess(MEAL2, false),
                MealsUtil.createWithExcess(MEAL1, false)));
        assertEquals(expected, actual);
    }

    @Test
    void testGetBetweenAllOnlyTime() throws Exception {
        String actual = getContent(mockMvc.perform(get(REST_URL + "?" + datesToParams(
                null,
                LocalTime.of(9, 40, 20),
                null,
                LocalTime.of(18, 0)
        ))));
        String expected = writeValue(List.of(
                MealsUtil.createWithExcess(MEAL5, true),
                MealsUtil.createWithExcess(MEAL4, true),
                MealsUtil.createWithExcess(MEAL2, false),
                MealsUtil.createWithExcess(MEAL1, false)));
        assertEquals(expected, actual);
    }

    @Test
    void testGetBetweenAllHalfDateHalfTime() throws Exception {
        String actual = getContent(mockMvc.perform(get(REST_URL + "?" + datesToParams(
                null,
                LocalTime.of(14, 0),
                LocalDate.of(2015, 5, 30),
                null
        ))));
        assertEquals(writeValue(Collections.singletonList(MealsUtil.createWithExcess(MEAL3, false))), actual);
    }

    private static String datesToParams(LocalDate startDate,
                                        LocalTime startTime,
                                        LocalDate endDate,
                                        LocalTime endTime) {
        List<String> params = new ArrayList<>();
        if (startDate != null) {
            params.add("startDate=" + startDate.format(StringToLocalDate.DATE_TIME_FORMATTER));
        }
        if (startTime != null) {
            params.add("startTime=" + startTime.format(StringToLocalTime.DATE_TIME_FORMATTER));
        }
        if (endDate != null) {
            params.add("endDate=" + endDate.format(StringToLocalDate.DATE_TIME_FORMATTER));
        }
        if (endTime != null) {
            params.add("endTime=" + endTime.format(StringToLocalTime.DATE_TIME_FORMATTER));
        }
        return String.join("&", params);
    }
}
