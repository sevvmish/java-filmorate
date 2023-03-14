package ru.yandex.practicum.filmorate.storage.Dao;

import java.util.Set;

public interface LikesDao {
    Set<Integer> getLikes(Integer id);

    void addLike(Integer filmId, Integer userId);


    void deleteLike(Integer filmId, Integer userId);

}
