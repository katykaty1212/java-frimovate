package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}", user.getLogin());

        validateName(user);
        user.setId(getNextId());
        log.info("Пользователю присвоен ID: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь создан.\n ID: {}\nЛогин: {}\nИмя: {}\nEmail: {}\nДата рождения: {}",
                user.getId(), user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {

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