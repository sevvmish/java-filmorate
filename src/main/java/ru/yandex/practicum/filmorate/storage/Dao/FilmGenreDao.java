package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmGenreDao {
    void updateGenreByFilm(Film film);
}
