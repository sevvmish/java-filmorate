package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int idGenerator;
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
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

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("wrong id: no such user to update");
            throw new ObjectNotFoundException("wrong id: no such user to update");
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

    @Override
    public User getById(Integer id) {
        if (!users.containsKey(id)) {
            log.warn("user with such Id do not exists");
            throw new ObjectNotFoundException("user with such Id do not exists");
        } else {
            return users.get(id);
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

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return true;
    }

    private int getNextId() {
        return ++idGenerator;
    }
}
