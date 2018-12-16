package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import ru.javawebinar.topjava.TestUtil;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.readFromJsonResultActions;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.util.exception.ErrorType.VALIDATION_ERROR;

class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Test
    void testGet() throws Exception {
        mockMvc.perform(get(REST_URL + ADMIN_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(getUserMatcher(ADMIN));
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + 1)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testGetByEmail() throws Exception {
        mockMvc.perform(get(REST_URL + "by?email=" + ADMIN.getEmail())
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(getUserMatcher(ADMIN));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + USER_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(userService.getAll(), ADMIN);
    }

    @Test
    void testDeleteNotFound() throws Exception {
        mockMvc.perform(delete(REST_URL + 1)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testGetUnAuth() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetForbidden() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdate() throws Exception {
        User updated = new User(USER);
        updated.setName("UpdatedName");
        updated.setRoles(Collections.singletonList(Role.ROLE_ADMIN));
        mockMvc.perform(put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isNoContent());

        assertMatch(userService.get(USER_ID), updated);
    }

    @Test
    void testUpdateShortPassword() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL + USER_ID,
                VALIDATION_ERROR,
                List.of("password size must be between 5 and 100"));
        User updated = new User(USER);
        updated.setPassword("shrt");
        ResultActions action = mockMvc.perform(put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        Assertions.assertEquals(expected, returned);
        assertMatch(userService.getAll(), ADMIN, USER);
    }

    @Test
    void testUpdateBadEmail() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL + USER_ID,
                VALIDATION_ERROR,
                List.of("email must be a well-formed email address"));
        User updated = new User(USER);
        updated.setEmail("not_a_email");
        ResultActions action = mockMvc.perform(put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        Assertions.assertEquals(expected, returned);
        assertMatch(userService.getAll(), ADMIN, USER);
    }

    @Test
    void testUpdateShortName() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("name size must be between 2 and 100"));
        User updated = new User(USER);
        updated.setName("U");

        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        Assertions.assertEquals(expected, returned);
        assertMatch(userService.getAll(), ADMIN, USER);
    }

    @Test
    void testCreate() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(expected, "newPass")))
                .andExpect(status().isCreated());

        User returned = readFromJsonResultActions(action, User.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(userService.getAll(), ADMIN, expected, USER);
    }

    @Test
    void testCreateShortPassword() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("password size must be between 5 and 100"));
        User created = new User(null, "New", "new@gmail.com", "shrt", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(created, created.getPassword())))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        Assertions.assertEquals(expected, returned);
        assertMatch(userService.getAll(), ADMIN, USER);
    }

    @Test
    void testCreateBadEmail() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("email must be a well-formed email address"));
        User created = new User(null, "New", "not_a_email", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(created, created.getPassword())))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        Assertions.assertEquals(expected, returned);
        assertMatch(userService.getAll(), ADMIN, USER);
    }

    @Test
    void testCreateShortName() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("name size must be between 2 and 100"));
        User created = new User(null, "U", "new@gmail.com", "newPass", 2300, Role.ROLE_USER, Role.ROLE_ADMIN);
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(created, created.getPassword())))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        Assertions.assertEquals(expected, returned);
        assertMatch(userService.getAll(), ADMIN, USER);
    }

    @Test
    void testGetAll() throws Exception {
        TestUtil.print(mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(getUserMatcher(ADMIN, USER)));
    }
}