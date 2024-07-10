package ru.yandex.practicum.filmorate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserController userController;

    private Map<Long, User> users;

    @BeforeEach
    public void setup() {
        users = new HashMap<>();
        when(userController.getAll()).thenReturn(users.values());
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setLogin("testLogin");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userController.create(any(User.class))).thenAnswer(invocation -> {
            User createdUser = invocation.getArgument(0);
            createdUser.setId(1L);
            users.put(createdUser.getId(), createdUser);
            return createdUser;
        });

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.login").value("testLogin"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setLogin("testLogin");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        users.put(user.getId(), user);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setLogin("updatedLogin");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userController.update(any(User.class))).thenAnswer(invocation -> {
            User newUser = invocation.getArgument(0);
            User existingUser = users.get(newUser.getId());
            existingUser.setEmail(newUser.getEmail());
            existingUser.setLogin(newUser.getLogin());
            existingUser.setName(newUser.getName());
            existingUser.setBirthday(newUser.getBirthday());
            return existingUser;
        });

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("updatedLogin"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setLogin("testLogin1");
        user1.setEmail("test1@example.com");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setLogin("testLogin2");
        user2.setEmail("test2@example.com");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        users.put(1L, user1);
        users.put(2L, user2);

        when(userController.getAll()).thenReturn(users.values());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("testLogin1"))
                .andExpect(jsonPath("$[1].login").value("testLogin2"));
    }
}

