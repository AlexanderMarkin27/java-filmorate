package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    boolean containsUser(long id);

    boolean isEmailDuplicated(String email, Long userId);

    User getUserById(long userId);
}
