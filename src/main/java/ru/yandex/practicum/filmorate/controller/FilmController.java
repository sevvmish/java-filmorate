package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int idGenerator;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
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

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("wrong id: no such film to update");
            throw new ValidationException("wrong id: no such film to update");
        } else {
            if (!isValidationChecked(film)) {
                throw new ValidationException("data validation error");
            } else {
                films.put(film.getId(), film);
                log.info("successfully updated film: {}", film.getName());
                return film;
            }
        }
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
