package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreDao {
    Set<Genre> findAllByFilmId(Integer id);

    void addGenreToFilm(Integer filmId, Genre genre);

    void updateGenreByFilm(Film film);
}
