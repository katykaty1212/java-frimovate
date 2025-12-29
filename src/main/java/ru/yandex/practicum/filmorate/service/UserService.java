package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    public final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User delete(Long userId) {
        return userStorage.delete(userId);
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", userId);
                    return new NotFoundException("Пользователь с ID " + userId + " не найден");
                });
    }

    public void addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        log.info("Попытка пользователей {} и {} подружиться", user, friend);

        if (user.getFriendsUserId().contains(friendId)) {
            log.warn("Пользователь {} уже в друзьях у {}", friendId, id);
            throw new ValidationException("Пользователи уже друзья");
        }

        if (id.equals(friendId)) {
            log.warn("Попытка добавить себя в друзья: {}", id);
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        user.getFriendsUserId().add(friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", user, friend);
        friend.getFriendsUserId().add(id);
        log.info("А пользователь {} добавил в друзья пользователя {}", friend, user);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        log.info("Попытка пользователей {} удалить из друзей {}", user, friend);

        user.getFriendsUserId().remove(friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", user, friend);
        friend.getFriendsUserId().remove(id);
        log.info("А пользователь {} удалил из друзей пользователя {}", friend, user);
    }

    public List<User> getListUserFriend(Long id) {

        User user = getUserById(id);

        log.info("Попытка получить список друзей");
        return user.getFriendsUserId().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        log.info("Попытка получить список общих друзей пользователей {} и {}", user, otherUser);

        Set<Long> common = new HashSet<>(user.getFriendsUserId());
        common.retainAll(otherUser.getFriendsUserId());

        return common.stream()
                .map(this::getUserById)
                .toList();
    }
}