package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User validUserForUpdate;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setEmail("testUser.email@example.com");
        testUser.setLogin("testUser");
        testUser.setName("test user Name");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void createUser_validData_userCreated() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.login").value(testUser.getLogin()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.birthday").value(testUser.getBirthday().toString()));
    }

    @Test
    public void updateUser_validData_userUpdated() throws Exception {
        testUser.setEmail("testUserForUpdate.email@example.com");

        ResultActions resultActions = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)));

        MvcResult result = resultActions.andReturn();
        String responseBody = result.getResponse().getContentAsString();

        Long userId = JsonPath.parse(responseBody).read("$.id", Long.class);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("updated.email@example.com");
        updatedUser.setLogin("Updatedlogin");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(1988, 10, 1));

        resultActions = mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.login").value(updatedUser.getLogin()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.birthday").value(updatedUser.getBirthday().toString()));
    }

    @Test
    public void getAllUsers_responseStatusOK() throws Exception {
        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void createUser_invalidEmail_validationFails() throws Exception {
        testUser.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_emptyEmail_validationFails() throws Exception {
        testUser.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_emptyLogin_validationFails() throws Exception {
        testUser.setLogin(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_blankLogin_validationFails() throws Exception {
        testUser.setLogin("   ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_futureBirthday_validationFails() throws Exception {
        testUser.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_userIdIsNull_validationFails() throws Exception {
        testUser.setId(null);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

}
