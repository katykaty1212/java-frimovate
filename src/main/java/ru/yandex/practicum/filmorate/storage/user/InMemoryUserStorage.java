package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Qualifier("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Создание пользователя: {}", user.getLogin());

        validateName(user);
        user.setId(getNextId());
        log.info("Пользователю присвоен ID: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь создан.\n ID: {}\nЛогин: {}\nИмя: {}\nEmail: {}\nДата рождения: {}",
                user.getId(), user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        return user;
    }

    @Override
    public User update(User newUser) {

        if (newUser.getId() == null) {
            log.error("Попытка обновления пользователя без указания ID");
            throw new ValidationException("ID должен быть указан.");
        }

        validateName(newUser);

        User existingUser = users.get(newUser.getId());

        if (existingUser == null) {
            log.error("Пользователь с ID {} в списке не найден.", newUser.getId());
            throw new ValidationException("Пользователь с ID " + newUser.getId() + " не найден");
        }

        log.info("Пользователь с ID {} найден в списке.", existingUser.getId());

        existingUser.setName(newUser.getName());
        existingUser.setLogin(newUser.getLogin());
        existingUser.setEmail(newUser.getEmail());
        existingUser.setBirthday(newUser.getBirthday());

        log.info("Пользователь с ID: {} обновлен.\nЛогин: {}\nИмя: {}\nEmail: {}\nДата рождения: {}",
                existingUser.getId(), existingUser.getLogin(), existingUser.getName(), existingUser.getEmail(),
                existingUser.getBirthday());

        return existingUser;
    }

    @Override
    public User delete(Long userId) {

        User user = users.get(userId);
        log.info("Попытка удаления пользователя {}", user.getName());

        if (user == null) {
            throw new NotFoundException("Такой пользователь не найден.");
        }

        users.remove(userId);
        log.info("Пользователь {} удален из друзей", user.getName());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

    }

    @Override
    public void acceptFriend(Long userId, Long friendId) {

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {

    }

    @Override
    public List<User> getUserFriends(Long userId) {
        return List.of();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return List.of();
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Попытка создания пользователя без имени. Вместо имени присваивается логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        log.info("Валидация имени прошла успешно.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}