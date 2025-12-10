package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    public UserController userController = new UserController();

    @Test
    public void createAndFindAllUsersTest() {
        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1login");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2login");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 5, 15));

        userController.create(user1);
        userController.create(user2);

        // Получаем всех пользователей
        Collection<User> allUsers = userController.findAll();

        assertEquals(2, allUsers.size(), "Должны вернуться 2 пользователя");
    }

    @Test
    public void createUserTest() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assertNotNull(createdUser.getId(), "Пользователь должен получить ID");
        assertEquals("test@mail.ru", createdUser.getEmail());
        assertEquals("validlogin", createdUser.getLogin());
        assertEquals("Valid Name", createdUser.getName());
    }

    @Test
    public void createUserWithEmptyNameTest() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("validlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assertEquals("validlogin", createdUser.getName(),
                "Имя должно замениться на логин при пустом имени");
    }

    @Test
    public void createUserWithNullNameTest() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("validlogin");
        user.setName(null); // null имя
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assertEquals("validlogin", createdUser.getName(),
                "Имя должно замениться на логин при null имени");
    }

    @Test
    public void updateUserWithValidDataTest() {
        User user = new User();
        user.setEmail("original@mail.ru");
        user.setLogin("originallogin");
        user.setName("Original Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);
        Long userId = createdUser.getId();

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(1995, 5, 15));

        User resultUser = userController.update(updatedUser);

        assertEquals(userId, resultUser.getId(), "ID должен остаться прежним");
        assertEquals("updated@mail.ru", resultUser.getEmail(), "Email должен обновиться");
        assertEquals("updatedlogin", resultUser.getLogin(), "Логин должен обновиться");
        assertEquals("Updated Name", resultUser.getName(), "Имя должно обновиться");
        assertEquals(LocalDate.of(1995, 5, 15),
                resultUser.getBirthday(), "Дата рождения должна обновиться");
    }

    @Test
    public void updateUserWithEmptyNameTest() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("originallogin");
        user.setName("Original Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("test@mail.ru");
        updatedUser.setLogin("updatedlogin");
        updatedUser.setName("");
        updatedUser.setBirthday(LocalDate.of(2000, 1, 1));

        User resultUser = userController.update(updatedUser);

        assertEquals("updatedlogin", resultUser.getName(),
                "Имя должно замениться на логин при пустом имени");
    }
}