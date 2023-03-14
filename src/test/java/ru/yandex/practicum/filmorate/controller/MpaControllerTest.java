package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Dao.MpaDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MpaControllerTest {
    private ResponseEntity<Mpa> response;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MpaDao mpaDao;

    @Test
    @DisplayName("Тест на получение полного списка рейтингов")
    void getAllTest() {
        ResponseEntity<Mpa[]> response = restTemplate.getForEntity("/mpa", Mpa[].class);
        List<Mpa> mpaList = Arrays.stream(response.getBody()).collect(Collectors.toList());
        assertEquals(mpaList, mpaDao.getAll());
    }

    @Test
    @DisplayName("Тест на получение рейтинга по ИД")
    void getByIdTest() {
        //2 - PG
        response = restTemplate.getForEntity("/mpa/2", Mpa.class);
        assertEquals(response.getBody(), mpaDao.getById(2));

        //4 - R
        response = restTemplate.getForEntity("/mpa/4", Mpa.class);
        assertEquals(response.getBody(), mpaDao.getById(4));
    }

    @Test
    @DisplayName("Тест на получение рейтинга по несуществующему ИД")
    void getByWrongIdTest() {
        response = restTemplate.getForEntity("/mpa/12345", Mpa.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}
