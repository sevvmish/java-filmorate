package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Dao.FriendsDao;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsDao friendsDao;

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
        user.getFriends().add(friend.getId());
        userStorage.update(user);
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
        user.getFriends().remove(friend.getId());
        userStorage.update(user);
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
        List<User> result = new ArrayList<>();
        for (Integer userId : user.getFriends()) {
            result.add(userStorage.getById(userId));
        }
        return result;
    }

    public List<User> getCommon(Integer id, Integer otherId) {
        if (id == null || otherId == null) {
            log.warn("error in id data");
            throw new ValidationException("error in id data");
        }
        User user = userStorage.getById(id);
        User otherUser = userStorage.getById(otherId);
        if (user == null || otherUser == null) {
            throw new ObjectNotFoundException("user not found");
        }
        return friendsDao.getCommon(id, otherId);
    }
}
