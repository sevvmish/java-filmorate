package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendsDao {
    Set<Integer> getFriends(Integer id);
    void insertFriends(User user);
    void deleteFriends(User user);
    List<User> getCommon(Integer id, Integer otherId);
}
