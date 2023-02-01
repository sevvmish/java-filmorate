package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<Film> response;
    private Film film1;
    private Film film2;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film();
        film1.setName("cats-killers");
        film1.setDescription("description of horror cats");
        film1.setReleaseDate(LocalDate.of(1950, 11, 11));
        film1.setDuration(60);

        film2 = new Film();
        film2.setName("cats-savers");
        film2.setDescription("description of holy cats");
        film2.setReleaseDate(LocalDate.of(2001, 11, 11));
        film2.setDuration(42);
    }

    @Test
    @DisplayName("Тест на добавление пустого запроса")
    void addEmptyFilmTest() {
        Film newFilm = new Film();
        response = getPostResponse(newFilm);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на добавление фильма и получения ИД")
    void addFilmTest() {
        response = getPostResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getId(), 1);
    }

    @Test
    @DisplayName("Тест на ошибку при добавлении фильма с нулевым или пустым именем")
    void nullOrEmptyNameErrorTest() {
        film1.setName(null);
        response = getPostResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        film2.setName("");
        response = getPostResponse(film2);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на ошибку из-за описания длинее 200 символов")
    void longDescriptionErrorTest() {
        String newDescription = film1.getDescription().repeat(200);
        film1.setDescription(newDescription);
        response = getPostResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на ошибку в дате релиза")
    void releaseDateErrorTest() {
        film1.setReleaseDate(LocalDate.of(1890, 10, 10));
        response = getPostResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Тест на ошибку в указании длительности фильма")
    void durationErrorTest() {
        film1.setDuration(-1);
        response = getPostResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на ошибку при обновлении фильма, которого не было ранее по его ИД")
    void updateWrongFilmErrorTest() {
        response = getPutResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Тест на ошибку валидации при обновлении фильма на новые данные")
    void updateFilmWithNewBadDataErrorTest() {
        response = getPostResponse(film1);
        film2.setId(response.getBody().getId());
        film2.setDuration(-2);
        response = getPutResponse(film2);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Film> getPostResponse(Film film) {
        return restTemplate.postForEntity("/films", film, Film.class);
    }

    private ResponseEntity<Film> getPutResponse(Film film) {
        HttpEntity<Film> entity = new HttpEntity<>(film);
        return restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class, film.getId());
    }
}