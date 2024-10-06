package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
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

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Добавление друга с ID {} для пользователя с ID {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Удаление друга с ID {} у пользователя с ID {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Запрос общих друзей между пользователем {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
