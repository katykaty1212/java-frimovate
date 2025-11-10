package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Попытка создания фильма {}", film.getName());
        validateFilmDates(film);

        film.setId(getNextId());
        log.info("Фильму {} присвоен ID: {}", film.getName(), film.getId());
        films.put(film.getId(), film);
        log.info("Фильм {} создан и добавлен в список.", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("Попытка обновления фильма без указания ID");
            throw new ValidationException("ID должен быть указан.");
        }

        validateFilmDates(newFilm);

        Film existingFilm = films.get(newFilm.getId());


        if (existingFilm == null) {
            log.error("Фильм с ID {} в списке не найден.", newFilm.getId());
            throw new ValidationException("Фильм с ID " + newFilm.getId() + " не найден");
        }

        log.info("Фильм с ID {} найден в списке фильмов.", existingFilm.getId());

        existingFilm.setName(newFilm.getName());
        existingFilm.setDescription(newFilm.getDescription());
        existingFilm.setDuration(newFilm.getDuration());
        existingFilm.setReleaseDate(newFilm.getReleaseDate());
        log.info("Фильм {} с ID {} успешно обновлен.", existingFilm.getName(), existingFilm.getId());
        return existingFilm;
    }

    private void validateFilmDates(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка создания фильма с некорректной датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getReleaseDate().isAfter(LocalDate.now())) {
            log.warn("Попытка создания фильма с некорректной датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не позже сегодня.");
        }

        log.info("Дата релиза {} прошла валидацию.", film.getReleaseDate());
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}