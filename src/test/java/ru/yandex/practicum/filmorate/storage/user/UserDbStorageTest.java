package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    private User makeTestUser() {
        User user = new User();
        user.setEmail("test" + System.currentTimeMillis() + "@mail.ru"); // уникальный email
        user.setLogin("testlogin" + System.currentTimeMillis());
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void shouldCreateUser() {
        User user = makeTestUser();
        User created = userStorage.create(user);
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void shouldFindUserById() {
        User user = makeTestUser();
        User created = userStorage.create(user);
        Optional<User> found = userStorage.getUserById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        User user = makeTestUser();
        User created = userStorage.create(user);
        created.setName("Новое имя");
        User updated = userStorage.update(created);
        assertThat(updated.getName()).isEqualTo("Новое имя");
    }

    @Test
    void shouldDeleteUser() {
        User user = makeTestUser();
        User created = userStorage.create(user);
        userStorage.delete(created.getId());
        Optional<User> found = userStorage.getUserById(created.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldAddAndAcceptFriend() {
        User user1 = userStorage.create(makeTestUser());
        User user2 = userStorage.create(makeTestUser());

        userStorage.addFriend(user1.getId(), user2.getId());
        User found1 = userStorage.getUserById(user1.getId()).get();
        assertThat(found1.getFriendships()).hasSize(1);
        assertThat(found1.getFriendships().iterator().next().getFriendId()).isEqualTo(user2.getId());

        userStorage.acceptFriend(user1.getId(), user2.getId());
        // проверить статус CONFIRMED (если нужно)
    }
}