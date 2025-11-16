package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}