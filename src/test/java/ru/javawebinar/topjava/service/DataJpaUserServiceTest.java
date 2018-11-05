package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Collections;
import java.util.Date;

import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.assertMatch;

@ActiveProfiles(Profiles.DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getAndCheckMeals() {
        User user = service.getWithMeal(USER_ID);
        assertMatch(user, USER);
        assertMatch(user.getMeals(), USER.getMeals());
    }

    @Test
    public void getAndCheckEmptyMeal() {
        User newUser = new User(null, "New", "new@gmail.com", "newPass", 1555, false, new Date(), Collections.singleton(Role.ROLE_USER));
        newUser.setId(service.create(newUser).getId());
        User user = service.getWithMeal(newUser.getId());
        assertMatch(user, newUser);
        assertMatch(user.getMeals(), newUser.getMeals());
    }
}
