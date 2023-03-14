package ru.yandex.practicum.filmorate.storage.Dao;

import java.util.Set;

public interface LikesDao {
    Set<Integer> getLikes(Integer id);

    void insertLikes(Integer filmId, Integer userId);


    void deleteLikes(Integer filmId, Integer userId);

}
