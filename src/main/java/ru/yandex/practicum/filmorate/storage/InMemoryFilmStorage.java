package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int idGenerator;
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        if (!isValidationChecked(film)) {
            throw new ValidationException("data validation error");
        } else {
            int id = getNextId();
            film.setId(id);
            films.put(id, film);
            log.info("new film added: {}", film.getName());
            return film;
        }
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("wrong id: no such film to update");
            throw new ObjectNotFoundException("wrong id: no such film to update");
        }
        if (isValidationChecked(film)) {
            films.put(film.getId(), film);
            log.info("successfully updated film: {}", film.getName());
            return film;
        } else {
            throw new ValidationException("validation exception");
        }
    }

    @Override
    public Film getById(Integer id) {
        if (!films.containsKey(id)) {
            log.warn("film with such Id do not exists");
            throw new ObjectNotFoundException("film with such Id do not exists");
        } else {
            return films.get(id);
        }
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        return null;
    }

    private boolean isValidationChecked(Film film) {
        if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            log.warn("release date is wrong");
            return false;
        }
        return true;
    }

    private int getNextId() {
        return ++idGenerator;
    }
}
