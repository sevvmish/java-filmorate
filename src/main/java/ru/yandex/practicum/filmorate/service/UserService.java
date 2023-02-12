package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendId) {
        if (id == null || friendId == null) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new ObjectNotFoundException("user not found");
        }
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        log.info("user {} got new friend {} in friends", id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        if (id == null || friendId == null) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new ObjectNotFoundException("user not found");
        }
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        log.info("user {} removed friend {} from friends", id, friendId);
    }

    public List<User> getFriends(Integer id) {
        if (id == null) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }
        User user = userStorage.getById(id);
        if (user == null) {
            throw new ObjectNotFoundException("user not found");
        }

        return new ArrayList<>(user.getFriends());
    }







    public List<User> getAll() {
        return userStorage.getAll();
    }


    public User add(User user) {
        return userStorage.add(user);
    }


    public User update(User user) {
        return userStorage.update(user);
    }


    public User getById(Integer id) {
        return userStorage.getById(id);
    }
}
