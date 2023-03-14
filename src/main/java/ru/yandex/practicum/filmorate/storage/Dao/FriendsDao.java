package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsDao {
    List<User> getFriends(Integer id);

    void insertFriends(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    List<User> getCommon(Integer id, Integer otherId);
}
