package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenreControllerTest {
    private ResponseEntity<Genre> response;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreDao genreDao;

    @Test
    @DisplayName("Тест на получение полного списка жанров")
    void getAllTest() {
        ResponseEntity<Genre[]> response = restTemplate.getForEntity("/genres", Genre[].class);
        List<Genre> genres = Arrays.stream(response.getBody()).collect(Collectors.toList());
        assertEquals(genres, genreDao.getAll());
    }

    @Test
    @DisplayName("Тест на получение жанра по ИД")
    void getByIdTest() {
        //2 - драма
        response = restTemplate.getForEntity("/genres/2", Genre.class);
        assertEquals(response.getBody(), genreDao.getById(2));

        //6 - боевик
        response = restTemplate.getForEntity("/genres/6", Genre.class);
        assertEquals(response.getBody(), genreDao.getById(6));
    }
}
