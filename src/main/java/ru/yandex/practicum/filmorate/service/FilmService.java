package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Dao.FilmDao;
import ru.yandex.practicum.filmorate.storage.Dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.Dao.LikesDao;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDao filmStorage;
    private final FilmGenreDao filmGenre;
    private final LikesDao likesDao;
    private static final LocalDate EXTREME_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        checkValidation(film);
        film = filmStorage.add(film);
        filmGenre.updateGenreByFilm(film);
        return film;
    }

    public Film update(Film film) {
        checkValidation(film);
        film = filmStorage.update(film);
        filmGenre.updateGenreByFilm(film);
        return film;
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId < 1 || userId < 1) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }

        likesDao.addLike(filmId, userId);
        log.info("like added to a film {} by user {}", filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (filmId < 1 || userId < 1) {
            log.warn("no such film or user");
            throw new ObjectNotFoundException("no such film or user");
        }
        likesDao.deleteLike(filmId, userId);
        log.info("like removed from film {} by user {}", filmId, userId);
    }

    public List<Film> getMostPopular(Integer count) {
        return filmStorage.getMostPopular(count);
    }

    private void checkValidation(Film film) {
        if (EXTREME_RELEASE_DATE.isAfter(film.getReleaseDate())) {
            log.warn("release date is wrong");
            throw new ValidationException("data validation error");
        }
    }

}
