package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.info("Получение списка всех фильмов. Всего фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Попытка создания фильма {}", film.getName());

        film.setId(getNextId());
        log.info("Фильму {} присвоен ID: {}", film.getName(), film.getId());

        films.put(film.getId(), film);
        log.info("Фильм {} создан и добавлен в список.", film.getName());

        return film;
    }

    @Override
    public Film update(Film newFilm) {

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

        if (existingFilm.getName() == null || existingFilm.getDescription() == null
                || existingFilm.getDuration() == null || existingFilm.getReleaseDate() == null) {
            log.error("Фильм с ID {} не прошел валидацию при обновлении.", newFilm.getId());
            throw new ValidationException("Новый фильм не прошел валидацию.");
        }

        existingFilm.setName(newFilm.getName());
        existingFilm.setDescription(newFilm.getDescription());
        existingFilm.setDuration(newFilm.getDuration());
        existingFilm.setReleaseDate(newFilm.getReleaseDate());
        log.info("Фильм {} с ID {} успешно обновлен.", existingFilm.getName(), existingFilm.getId());

        return existingFilm;
    }

    @Override
    public Film delete(Long id) {
        Film film = films.get(id);
        log.info("Попытка удаления фильма {}", film.getName());

        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        films.remove(id);
        log.info("Фильм {} удален из списка фильмов", film.getName());

        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
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