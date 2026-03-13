package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film delete(Long id) {
        return filmStorage.delete(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> {
                    log.error("Фильм с ID {} не найден", filmId);
                    return new NotFoundException("Фильм с ID " + filmId + " не найден");
                });
    }

    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.deleteLike(filmId, userId);

        log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilm(Long count) {
        log.info("Попытка получение {} популярных фильмов", count);
        Collection<Film> allFilms = filmStorage.findAll();

        if (allFilms.isEmpty()) {
            log.warn("Список фильмов пуст");
            return Collections.emptyList(); // вернуть пустой список
        }

        return allFilms.stream()
                .sorted((f1, f2) ->
                        f2.getLikeUserId().size() - f1.getLikeUserId().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}