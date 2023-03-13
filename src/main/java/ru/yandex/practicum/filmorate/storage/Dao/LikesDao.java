package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface LikesDao {
    Set<Integer> getLikes(Film film);
    void insertLikes(Film film);
    void updateLikes(Film film);
    void deleteLikes(Film film);

}
