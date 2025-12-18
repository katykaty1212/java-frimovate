package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    public final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        log.info("Попытка пользователей {} и {} подружиться", user, friend);

        if (user == null) {
            log.error("Не найден пользователь {}", user);
            throw new NotFoundException("Не найден пользователь.");
        }

        if (friend == null) {
            log.error("Не найден пользователь {}", friend);
            throw new NotFoundException("Не найден другой пользователь.");
        }
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
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        log.info("Попытка пользователей {} удалить из друзей {}", user, friend);

        if (user == null) {
            log.error("Не найден пользователь {}", user);
            throw new NotFoundException("Не найден пользователь.");
        }

        if (friend == null) {
            log.error("Не найден пользователь {}", friend);
            throw new NotFoundException("Не найден пользователь.");
        }

        user.getFriendsUserId().remove(friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", user, friend);
        friend.getFriendsUserId().remove(id);
        log.info("А пользователь {} удалил из друзей пользователя {}", friend, user);
    }

    public List<User> getListUserFriend(Long id) {

        User user = userStorage.getUserById(id);

        if (user == null) {
            log.error("Не найден пользователь {}", user);
            throw new NotFoundException("Не найден пользователь.");
        }

        log.info("Попытка получить список друзей");
        return user.getFriendsUserId().stream().map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id);
        User otherUser = userStorage.getUserById(otherId);
        log.info("Попытка получить список общих друзей пользователей {} и {}", user, otherUser);

        if (user == null) {
            log.error("Не найден пользователь {}", user);
            throw new NotFoundException("Не найден пользователь.");
        }

        if (otherUser == null) {
            log.error("Не найден пользователь {}", otherUser);
            throw new NotFoundException("Не найден другой пользователь.");
        }

        Set<Long> common = new HashSet<>(user.getFriendsUserId());
        common.retainAll(otherUser.getFriendsUserId());

        return common.stream()
                .map(userStorage::getUserById)
                .toList();
    }
}