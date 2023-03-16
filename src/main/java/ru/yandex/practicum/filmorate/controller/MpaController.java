package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        if (id < 1) {
            log.warn("error in id while getting MPA by id");
            throw new ValidationException("error in id");
        }
        return mpaService.getById(id);
    }

    @GetMapping()
    public List<Mpa> getAll() {
        return mpaService.getAll();
    }
}
