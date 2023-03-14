package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Dao.FriendsDao;
import ru.yandex.practicum.filmorate.storage.Dao.UserDao;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userStorage;
    private final FriendsDao friendsDao;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User add(User user) {
        checkValidation(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        checkValidation(user);
        return userStorage.update(user);
    }

    public User getById(Integer id) {
        return userStorage.getById(id);
    }


    public void addFriend(Integer id, Integer friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        if (user == null || friend == null) {
            throw new ObjectNotFoundException("user not found");
        }
        friendsDao.insertFriends(id, friendId);
        log.info("user {} got new friend {} in friends", id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        friendsDao.deleteFriend(id, friendId);
        log.info("user {} removed friend {} from friends", id, friendId);
    }

    public List<User> getFriends(Integer id) {
        return friendsDao.getFriends(id);
    }

    public List<User> getCommon(Integer id, Integer otherId) {
        User user = userStorage.getById(id);
        User otherUser = userStorage.getById(otherId);
        if (user == null || otherUser == null) {
            throw new ObjectNotFoundException("user not found");
        }
        return friendsDao.getCommon(id, otherId);
    }

    private void checkValidation(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("whitespace in login");
            throw new ValidationException("data validation error");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
