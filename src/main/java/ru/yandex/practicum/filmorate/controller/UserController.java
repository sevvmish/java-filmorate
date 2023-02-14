package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    @GetMapping("{id}")
    public User getById(@PathVariable Integer id) {
        if (id < 1 || id == null) {
            log.warn("error in id while getting user by id");
            throw new ValidationException("error in user id");
        }
        return userStorage.getById(id);
    }

    @GetMapping
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userStorage.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommon(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommon(id, otherId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }
}
