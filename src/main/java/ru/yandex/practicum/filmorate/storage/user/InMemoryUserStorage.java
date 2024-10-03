package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0;

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (isEmailDuplicated(user.getEmail(), null)) {
            log.error("Имейл уже используется: {}", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        users.put(user.getId(), user);
        log.info("Создан юзер с ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        long userId = newUser.getId();
        if (!containsUser(userId)) {
            log.error("Юзер с ID {} не найден", userId);
            throw new NotFoundException("Юзер с id = " + userId + " не найден");
        }
        if (isEmailDuplicated(newUser.getEmail(), userId)) {
            log.error("Имейл уже используется: {}", newUser.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        users.replace(userId, newUser);
        log.info("Юзер с ID {} обновлен", userId);
        return newUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public boolean containsUser(long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean isEmailDuplicated(String email, Long userId) {
        return users.values().stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getEmail().equals(email));
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
