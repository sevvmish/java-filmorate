package ru.yandex.practicum.filmorate.storage.Dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    List<User> getAll();

    User add(User user);

    User update(User user);

    User getById(Integer id);
}
