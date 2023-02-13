package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }
        if (userStorage.getById(userId) == null) {
            log.warn("no such user with id {}", userId);
            throw new ObjectNotFoundException("user not found");
        }
        Film film = filmStorage.getById(filmId);
        film.getLikes().add(userId);
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
        log.info("like removed from film {} by user {}", filmId, userId);
    }

    public List<Film> getMostPopular(Integer count) {
        if (filmStorage.getAll().isEmpty()) return new ArrayList<>();
        if (count == 0 || count == null) count = 10;

        return filmStorage.getAll().stream().sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count).collect(Collectors.toList());
    }

}
