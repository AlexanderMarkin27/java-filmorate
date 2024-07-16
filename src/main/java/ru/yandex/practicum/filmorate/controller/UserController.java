package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0;

    @PostMapping
    public User create(@RequestBody @Validated(BasicInfo.class) User user) {
        log.info("Реквест на создание юзера: {}", user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        boolean userWithDuplicatedEmail = users.values().stream()
                .anyMatch(item -> item.getEmail().equals(user.getEmail()));

        if (userWithDuplicatedEmail) {
            log.error("Имейл уже используется: {}", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        users.put(user.getId(), user);
        log.info("Создан юзер с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Validated(AdvanceInfo.class) User newUser) {
        log.info("Реквест на обновление юзера: {}", newUser);
        long userId = newUser.getId();
        if (!users.containsKey(userId)) {
            log.error("Юзер с ID {} не найден",userId);
            throw new NotFoundException("Юзер с id = " + userId + " не найден");
        }

        boolean userWithDuplicatedEmail = users.values().stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getEmail().equals(newUser.getEmail()));

        if (userWithDuplicatedEmail) {
            log.error("Имейл уже используется: {}", newUser.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        users.replace(userId, newUser);
        log.info("Юзер с ID ID {} обновлен", userId);
        return newUser;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Реквест на получение всех юзеров");
        return users.values();
    }

    private long getNextId() {
        return ++currentMaxId;
    }

}
