package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

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
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
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
        friend.getFriends().remove(user.getId());
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
        List<User> result = new ArrayList<>();
        for (Integer usersId : user.getFriends()) {
            if (otherUser.getFriends().contains(usersId)) {
                result.add(userStorage.getById(usersId));
            }
        }

        return result;
    }
}
