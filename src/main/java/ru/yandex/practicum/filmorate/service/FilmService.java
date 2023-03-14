package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null || filmId < 0 || userId < 0) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }

        if (userStorage.getById(userId) == null) {
            log.warn("no such user with id {}", userId);
            throw new ObjectNotFoundException("user not found");
        }
        Film film = filmStorage.getById(filmId);
        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("like added to a film {} by user {}", filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }
        if (userStorage.getById(userId) == null) {
            log.warn("no such user with id {}", userId);
            throw new ObjectNotFoundException("user not found");
        }
        Film film = filmStorage.getById(filmId);
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("like removed from film {} by user {}", filmId, userId);
    }

    public List<Film> getMostPopular(Integer count) {
        if (filmStorage.getAll().isEmpty()) return new ArrayList<>();
        if (count == 0 || count == null) count = 10;

        return filmStorage.getMostPopular(count);
    }

}
