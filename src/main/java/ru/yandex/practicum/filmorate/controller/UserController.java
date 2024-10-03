package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public User create(@RequestBody @Validated(BasicInfo.class) User user) {
        log.info("Реквест на создание юзера: {}", user);
        return userStorage.addUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Validated(AdvanceInfo.class) User newUser) {
        log.info("Реквест на обновление юзера: {}", newUser);
        return userStorage.updateUser(newUser);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Реквест на получение всех юзеров");
        return userStorage.getAllUsers();
    }
}
