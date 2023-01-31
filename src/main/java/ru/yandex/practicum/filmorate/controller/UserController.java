package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int idGenerator;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User add(@RequestBody User user) {
        if (!isValidationChecked(user)) {
            throw new ValidationException("data validation error");
        } else {

            int id = getNextId();
            user.setId(id);
            users.put(id, user);
            log.info("new user added: {}", user.getLogin());
            return user;
        }
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("wrong id: no such user to update");
            throw new ValidationException("wrong id: no such user to update");
        } else {
            if (!isValidationChecked(user)) {
                throw new ValidationException("data validation error");
            } else {
                users.put(user.getId(), user);
                log.info("successfully updated user: {}", user.getName());
                return user;
            }
        }
    }

    private boolean isValidationChecked(User user) {
        if (!user.getLogin().isEmpty() && !user.getLogin().isBlank()) {
            for (int i = 0; i < user.getLogin().length(); i++) {
                if (Character.isWhitespace(user.getLogin().charAt(i))) {
                    log.warn("whitespace in login");
                    return false;
                }
            }
        }
        if (user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("error in email");
            return false;
        } else if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.warn("error in login");
            return false;
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("error in birthday");
            return false;
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return true;
    }

    private int getNextId() {
        return ++idGenerator;
    }
}
