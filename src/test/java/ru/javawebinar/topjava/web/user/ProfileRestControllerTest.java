package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.TestUtil;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UserUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.*;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.util.exception.ErrorType.VALIDATION_ERROR;
import static ru.javawebinar.topjava.web.user.ProfileRestController.REST_URL;

class ProfileRestControllerTest extends AbstractControllerTest {

    @Test
    void testGet() throws Exception {
        TestUtil.print(
                mockMvc.perform(get(REST_URL)
                        .with(userHttpBasic(USER)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(getUserMatcher(USER))
        );
    }

    @Test
    void testGetUnAuth() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        assertMatch(userService.getAll(), ADMIN);
    }

    @Test
    void testRegister() throws Exception {
        UserTo createdTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);

        ResultActions action = mockMvc.perform(post(REST_URL + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(createdTo)))
                .andDo(print())
                .andExpect(status().isCreated());
        User returned = readFromJsonResultActions(action, User.class);

        User created = UserUtil.createNewFromTo(createdTo);
        created.setId(returned.getId());

        assertMatch(returned, created);
        assertMatch(userService.getByEmail("newemail@ya.ru"), created);
    }

    @Test
    void testRegisterBadEmail() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL + "/register",
                VALIDATION_ERROR,
                List.of("email must be a well-formed email address"));
        UserTo createdTo = new UserTo(null, "newName", "not_a_mail", "newPassword", 1500);

        ResultActions action = mockMvc.perform(post(REST_URL + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(createdTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        Assertions.assertThrows(NotFoundException.class, () -> userService.getByEmail(createdTo.getEmail()));
    }

    @Test
    void testRegisterShortName() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL + "/register",
                VALIDATION_ERROR,
                List.of("name size must be between 2 and 100"));
        UserTo createdTo = new UserTo(null, "U", "newemail@ya.ru", "newPassword", 1500);

        ResultActions action = mockMvc.perform(post(REST_URL + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(createdTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        Assertions.assertThrows(NotFoundException.class, () -> userService.getByEmail(createdTo.getEmail()));
    }

    @Test
    void testRegisterShortPassword() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL + "/register",
                VALIDATION_ERROR,
                List.of("password length must between 5 and 32 characters"));
        UserTo createdTo = new UserTo(null, "newName", "newemail@ya.ru", "shrt", 1500);

        ResultActions action = mockMvc.perform(post(REST_URL + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(createdTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        Assertions.assertThrows(NotFoundException.class, () -> userService.getByEmail(createdTo.getEmail()));
    }

    @Test
    void testUpdate() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "newemail@ya.ru", "newPassword", 1500);

        mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(userService.getByEmail("newemail@ya.ru"), UserUtil.updateFromTo(new User(USER), updatedTo));
    }

    @Test
    void testUpdateBadEmail() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("email must be a well-formed email address"));
        UserTo updatedTo = new UserTo(null, "newName", "not_a_e_mail", "newPassword", 1500);

        ResultActions action = mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        assertMatch(userService.getByEmail(USER.getEmail()), USER);
    }

    @Test
    void testUpdateShortPassword() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("password length must between 5 and 32 characters"));
        UserTo updatedTo = new UserTo(null, "newName", "newemail@ya.ru", "shrt", 1500);

        ResultActions action = mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        assertMatch(userService.getByEmail(USER.getEmail()), USER);
    }

    @Test
    void testUpdateShortName() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of("name size must be between 2 and 100"));
        UserTo updatedTo = new UserTo(null, "N", "newemail@ya.ru", "newPassword", 1500);

        ResultActions action = mockMvc.perform(put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        assertMatch(userService.getByEmail(USER.getEmail()), USER);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testUpdateDuplicateEmail() throws Exception {
        ErrorInfo expected = new ErrorInfo(
                "http://localhost" + REST_URL,
                VALIDATION_ERROR,
                List.of(messageSource.getMessage("user.error.duplicateEmail", new Object[]{}, Locale.ENGLISH)));
        UserTo updatedTo = new UserTo(null, "NewName", ADMIN.getEmail(), "newPassword", 1500);

        ResultActions action = mockMvc.perform(put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity());

        ErrorInfo returned = readFromJsonResultActions(action, ErrorInfo.class);

        assertThat(returned).isEqualToComparingFieldByField(expected);
        assertMatch(userService.getAll(), ADMIN, USER);
    }
}