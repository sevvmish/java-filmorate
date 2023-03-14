package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping("{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        if (id < 1 || id == null) {
            log.warn("error in id while getting Genre by id");
            throw new ValidationException("error in id");
        }
        return genreService.getById(id);
    }

    @GetMapping()
    public List<Genre> getAll() {
        return genreService.getAll();
    }
}
