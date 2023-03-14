package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    List<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    Film getById(Integer id);

    List<Film> getMostPopular(Integer count);
}
