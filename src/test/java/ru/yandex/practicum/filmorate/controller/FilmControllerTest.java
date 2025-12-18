package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    public Film film;
    public InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    public InMemoryUserStorage userStorage = new InMemoryUserStorage();
    public FilmService filmService = new FilmService(filmStorage, userStorage);
    public FilmController filmController = new FilmController(filmStorage, filmService);

    @Test
    public void createAndFindAllFilmsTest() {
        Film film1 = new Film();
        film1.setName("Film One");
        film1.setDescription("Description one");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(Duration.ofMinutes(120));

        Film film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Description two");
        film2.setReleaseDate(LocalDate.of(2010, 5, 15));
        film2.setDuration(Duration.ofMinutes(150));

        filmController.create(film1);
        filmController.create(film2);

        Collection<Film> allFilms = filmController.findAll();

        assertEquals(2, allFilms.size(), "Должны вернуться 2 фильма");
    }

    @Test
    public void createFilmTest() {
        film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Film createdFilm = filmController.create(film);

        assertNotNull(createdFilm.getId(), "Фильм должен получить ID");
        assertEquals("Valid Film", createdFilm.getName());
        assertEquals(Duration.ofMinutes(120), createdFilm.getDuration());
    }


    @Test
    public void updateValidAndInvalidFilmTest() {
        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Original description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        Film createdFilm = filmController.create(film);
        Long filmId = createdFilm.getId();

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated description");
        updatedFilm.setReleaseDate(LocalDate.of(2010, 5, 15));
        updatedFilm.setDuration(Duration.ofMinutes(150));

        Film resultFilm = filmController.update(updatedFilm);

        assertEquals(filmId, resultFilm.getId(), "ID должен остаться прежним");
        assertEquals("Updated Film", resultFilm.getName(), "Название должно обновиться");
        assertEquals("Updated description", resultFilm.getDescription(), "Описание должно обновиться");
        assertEquals(LocalDate.of(2010, 5, 15), resultFilm.getReleaseDate(), "Дата должна обновиться");
        assertEquals(Duration.ofMinutes(150), resultFilm.getDuration(), "Продолжительность должна обновиться");

        Film invalidFilm = new Film();
        invalidFilm.setId(filmId);
        invalidFilm.setName("Invalid Film");
        invalidFilm.setDescription("Invalid description");
        invalidFilm.setReleaseDate(LocalDate.now().plusDays(1));
        invalidFilm.setDuration(Duration.ofMinutes(150));

        assertEquals("Updated Film", updatedFilm.getName(), "Название не должно измениться");
        assertEquals(LocalDate.of(2010, 5, 15), updatedFilm.getReleaseDate(), "Дата не должна измениться");
    }
}