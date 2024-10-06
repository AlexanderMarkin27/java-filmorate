package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователь {} добавлен в друзья к {}", friendId, userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new NotFoundException("Пользователь " + friendId + " не является другом для " + userId);
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь {} удален из друзей у {}", friendId, userId);
    }

    public Set<User> getCommonFriends(long userId, long otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Long> commonFriendIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonFriendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    private User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }
}

