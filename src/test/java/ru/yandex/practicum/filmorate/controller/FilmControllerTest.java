package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private final FilmDbStorage filmDbStorage;
    private ResponseEntity<Film> response;
    private Film film1;
    private Film film2;
    private User user1;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film();
        film1.setName("cats-killers");
        film1.setDescription("description of horror cats");
        film1.setReleaseDate(LocalDate.of(1950, 11, 11));
        film1.setDuration(60);
        film1.setMpa(new Mpa(1, "G"));

        film2 = new Film();
        film2.setName("cats-savers");
        film2.setDescription("description of holy cats");
        film2.setReleaseDate(LocalDate.of(2001, 11, 11));
        film2.setDuration(42);
        film2.setMpa(new Mpa(1, "G"));

        user1 = new User();
        user1.setEmail("ivanov@gmail.com");
        user1.setLogin("ivanov");
        user1.setName("Ivan");
        user1.setBirthday(LocalDate.of(1988, 10, 11));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user1, User.class);
        user1.setId(response.getBody().getId());
    }

    @Test
    @DisplayName("Тест на добавление пустого запроса")
    void addEmptyFilmTest() {
        Film newFilm = new Film();
        response = getPostResponse(newFilm);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на добавление нового фильма и получение ИД")
    void addFilmTest() {
        Film filmTest = new Film();
        filmTest.setName("test cats killers");
        filmTest.setDescription("description of test cats");
        filmTest.setReleaseDate(LocalDate.of(1950, 11, 11));
        filmTest.setDuration(60);
        filmTest.setMpa(new Mpa(1, "G"));

        response = getPostResponse(filmTest);
        int id = response.getBody().getId();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), filmDbStorage.getById(id));
    }

    @Test
    @DisplayName("Тест на получение фильма по ИД")
    void getFilmTest() {
        response = getPostResponse(film1);
        int id = response.getBody().getId();

        response = restTemplate.getForEntity("/films/" + id, Film.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getId(), id);
        assertEquals(response.getBody(), filmDbStorage.getById(id));
    }

    @Test
    @DisplayName("Тест на обновление фильма")
    void updateFilmTest() {
        response = getPostResponse(film1);
        Film beforeUpdateFilm = response.getBody();

        beforeUpdateFilm.setName("new film");
        beforeUpdateFilm.setDescription("new description");
        response = getPutResponse(beforeUpdateFilm);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        response = restTemplate.getForEntity("/films/" + beforeUpdateFilm.getId(), Film.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), beforeUpdateFilm);
    }

    @Test
    @DisplayName("Тест на получение всех фильмов")
    void getAllFilmsTest() {
        response = getPostResponse(film1);
        response = getPostResponse(film2);

        ResponseEntity<Film[]> responseList = restTemplate.getForEntity("/films/", Film[].class);
        Film[] films = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        List<Film> filmsInDb = filmDbStorage.getAll();
        for (int i = 0; i < filmsInDb.size(); i++) {
            assertEquals(films[i], filmsInDb.get(i));
        }
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
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на ошибку в указании длительности фильма")
    void durationErrorTest() {
        film1.setDuration(-1);
        response = getPostResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        List<Film> all = filmDbStorage.getAll();
        System.out.println(all.size());
        for (int i = 0; i < all.size(); i++) {
            System.out.println(all.get(i));
        }
    }

    @Test
    @DisplayName("Тест на ошибку при обновлении фильма, которого не было ранее по его ИД")
    void updateWrongFilmErrorTest() {
        response = getPutResponse(film1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
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

    @Test
    @DisplayName("Тест на добавление лайка фильму")
    void addLikeTest() {
        //adding film
        response = getPostResponse(film1);
        int filmId = response.getBody().getId();

        HttpEntity<Film> entity = new HttpEntity<>(film1);
        response = restTemplate.exchange("/films/" + filmId + "/like/" + user1.getId(), HttpMethod.PUT, entity, Film.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        Film film = filmDbStorage.getById(filmId);
        //assertEquals(film.getLikes().toArray()[0], user1.getId());
    }

    @Test
    @DisplayName("Тест на удаление лайка")
    void removeLikeTest() {
        //adding film
        response = getPostResponse(film1);
        int filmId = response.getBody().getId();
        //adding like to a film
        HttpEntity<Film> entity = new HttpEntity<>(film1);
        response = restTemplate.exchange("/films/" + filmId + "/like/" + user1.getId(), HttpMethod.PUT, entity, Film.class);

        Film film = filmDbStorage.getById(filmId);
        //assertEquals(film.getLikes().toArray()[0], user1.getId());

        response = restTemplate.exchange("/films/" + filmId + "/like/" + user1.getId(), HttpMethod.DELETE, entity, Film.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        film = filmDbStorage.getById(filmId);
        //assertTrue(film.getLikes().isEmpty());
    }

    @Test
    @DisplayName("Тест на получение популярных фильмов")
    void getPopularFilmsTest() {
        //film2.getLikes().add(1);
        Film film1WithId = getPostResponse(film1).getBody();
        Film film2WithId = getPostResponse(film2).getBody();
        Film[] rightFilmOrder = new Film[]{film2WithId, film1WithId};

        ResponseEntity<Film[]> responseList = restTemplate.getForEntity("/films/popular", Film[].class);
        Film[] films = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        for (int i = 0; i < films.length; i++) {
            assertEquals(films[i], rightFilmOrder[i]);
        }
    }

    private ResponseEntity<Film> getPostResponse(Film film) {
        return restTemplate.postForEntity("/films", film, Film.class);
    }

    private ResponseEntity<Film> getPutResponse(Film film) {
        HttpEntity<Film> entity = new HttpEntity<>(film);
        return restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class, film.getId());
    }
}