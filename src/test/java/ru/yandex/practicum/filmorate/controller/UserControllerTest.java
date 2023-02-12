package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<User> response;
    private User user1;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        user1 = new User();
        user1.setEmail("ivanov@gmail.com");
        user1.setLogin("ivanov");
        user1.setName("Ivan");
        user1.setBirthday(LocalDate.of(1988, 10, 11));

        user2 = new User();
        user2.setEmail("petrov@gmail.com");
        user2.setLogin("petrov");
        user2.setName("Petr");
        user2.setBirthday(LocalDate.of(1982, 5, 11));
    }

    @Test
    @DisplayName("Тест на добавление пустого запроса")
    void addEmptyUserTest() {
        User newUser = new User();
        response = getPostResponse(newUser);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на получение юзера по ИД")
    void getFilmTest() {
        response = getPostResponse(user1);
        int id = response.getBody().getId();
        response = restTemplate.getForEntity("/users/1", User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getId(), 1);
    }

    @Test
    @DisplayName("Тест на ошибку при добавлении логина: нулевой, пустой, с пробелом")
    void loginErrorTest() {
        user1.setLogin(null);
        response = getPostResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        user1.setLogin("");
        response = getPostResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        user2.setLogin("Bad name");
        response = getPostResponse(user2);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на ошибку в имейле: нулевой, пустой, без @")
    void emailErrorTest() {
        user1.setEmail(null);
        response = getPostResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        user1.setEmail("");
        response = getPostResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

        user2.setEmail("bad-address.ru");
        response = getPostResponse(user2);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на не существующий день рождения")
    void birthdayErrorTest() {
        user1.setBirthday(LocalDate.of(2024, 10, 7));
        response = getPostResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("Тест на ошибку при обновлении юзера, которого не было ранее по его ИД")
    void updateWrongUserErrorTest() {
        response = getPutResponse(user1);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Тест на ошибку валидации при обновлении юзера на новые данные")
    void updateFilmWithNewBadDataErrorTest() {
        response = getPostResponse(user1);
        user2.setId(response.getBody().getId());
        user2.setEmail("another-bad.email");
        response = getPutResponse(user2);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на добавление друга")
    void addFriendTest() {
        response = getPostResponse(user1);
        int userId = response.getBody().getId();

        response = getPostResponse(user2);
        int friendId = response.getBody().getId();

        HttpEntity<User> entity = new HttpEntity<>(user1);
        response = restTemplate.exchange("/users/" + userId + "/friends/" + friendId, HttpMethod.PUT, entity, User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("Тест на удаление друга")
    void removeFriendTest() {
        response = getPostResponse(user1);
        int userId = response.getBody().getId();

        response = getPostResponse(user2);
        int friendId = response.getBody().getId();

        HttpEntity<User> entity = new HttpEntity<>(user1);
        restTemplate.exchange("/users/" + userId + "/friends/" + friendId, HttpMethod.PUT, entity, User.class);

        response = restTemplate.exchange("/users/" + userId + "/friends/" + friendId, HttpMethod.DELETE, entity, User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    private ResponseEntity<User> getPostResponse(User user) {
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<User> getPutResponse(User user) {
        HttpEntity<User> entity = new HttpEntity<>(user);
        return restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class, user.getId());
    }
}