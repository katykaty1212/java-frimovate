package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long id, Long userId) {
        log.info("Пользователь {} пытается поставить лайк фильму {}", userId, id);
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);

        if (film == null) {
            log.error("Фильм с ID {} не найден при попытке поставить лайк", id);
            throw new NotFoundException("Такого фильма не существует.");
        }

        if (user == null) {
            log.error("Пользователь с ID {} не найден при попытке поставить лайк фильму {}", userId, id);
            throw new NotFoundException("Такого пользователя не существует.");
        }

        film.getLikeUserId().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}",
                userId, id);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        log.info("Попытка пользователей {} удалить лайк у фильма {}", user, film);

        if (film == null) {
            log.error("Фильм с ID {} не найден при попытке удалить лайк", filmId);
            throw new NotFoundException("Такого фильма не существует.");
        }

        if (user == null) {
            log.error("Пользователь с ID {} не найден при попытке удалить лайк", userId);
            throw new NotFoundException("Такого пользователя не существует.");
        }

        film.getLikeUserId().remove(userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
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