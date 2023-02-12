package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    List<User> getAll();
    User add(User user);
    User update(User user);
    User getById(Integer id);
}
