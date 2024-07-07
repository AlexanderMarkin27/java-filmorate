package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Реквест на создание юзера: {}", user);
        validateUserData(user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Создан юзер с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Реквест на обновление юзера: {}", newUser);
        if (newUser == null || newUser.getId() == null) {
            log.error("Ошибка валидации: Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            log.error("Юзер с ID {} не найден", newUser.getId());
            throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
        }
        Optional<User> userWithDuplicatedEmail = users.values().stream()
                .filter(user -> !user.getId().equals(existingUser.getId()) && user.getEmail().equals(newUser.getEmail()))
                .findAny();
        if (userWithDuplicatedEmail.isPresent()) {
            log.error("Имейл уже используется: {}", newUser.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        validateUserData(newUser);
        updateUserData(existingUser, newUser);
        log.info("Юзер с ID ID {} обновлен", newUser.getId());
        return existingUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Реквест на получение всех юзеров");
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUserData(User user) {
        log.info("Валидация данных юзера: {}", user);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Ошибка валидации: Электронная почта не может быть пустой");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Ошибка валидации: Электронная почта должна содержать символ @");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Ошибка валидации: Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

    }

    private void updateUserData(User existingUser, User newUser) {
        log.info("Обновление данных юзера с ID: {}", existingUser.getId());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setName(newUser.getName());
        existingUser.setBirthday(newUser.getBirthday());
        existingUser.setLogin(newUser.getLogin());
    }
}
