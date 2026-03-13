package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class}) // Импортируем оба хранилища, которые нужны для тестов
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film makeTestFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));
        MPA mpa = new MPA();
        mpa.setId(1);
        film.setMpa(mpa);
        return film;
    }

    private User makeTestUser() {
        User user = new User();
        user.setEmail("test@film.ru");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void shouldCreateFilm() {
        Film film = makeTestFilm();
        Film created = filmStorage.create(film);
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void shouldFindFilmById() {
        Film film = makeTestFilm();
        Film created = filmStorage.create(film);
        Optional<Film> found = filmStorage.getFilmById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(film.getName());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = makeTestFilm();
        Film created = filmStorage.create(film);

        Film filmToUpdate = filmStorage.getFilmById(created.getId()).get();
        filmToUpdate.setName("Обновлённое название");

        Film updated = filmStorage.update(filmToUpdate);

        assertThat(updated.getName()).isEqualTo("Обновлённое название");
    }

    @Test
    void shouldDeleteFilm() {
        Film film = makeTestFilm();
        Film created = filmStorage.create(film);
        filmStorage.delete(created.getId());
        Optional<Film> found = filmStorage.getFilmById(created.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldAddAndRemoveLike() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User user = makeTestUser();
        User createdUser = userStorage.create(user);

        Film film = makeTestFilm();
        Film createdFilm = filmStorage.create(film);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());

        Film withLike = filmStorage.getFilmById(createdFilm.getId()).get();
        assertThat(withLike.getLikeUserId()).contains(createdUser.getId());

        filmStorage.deleteLike(createdFilm.getId(), createdUser.getId());

        Film withoutLike = filmStorage.getFilmById(createdFilm.getId()).get();
        assertThat(withoutLike.getLikeUserId()).doesNotContain(createdUser.getId());
    }
}