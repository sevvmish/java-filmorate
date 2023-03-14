package ru.yandex.practicum.filmorate.controller;

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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<User> response;
    private final UserDbStorage userDbStorage;
    private User user1;
    private User user2;
    private User user3;
    private User user4;

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

        user3 = new User();
        user3.setEmail("sidorov@gmail.com");
        user3.setLogin("sidorov");
        user3.setName("Sidor");
        user3.setBirthday(LocalDate.of(1990, 5, 7));

        user4 = new User();
        user4.setEmail("last@gmail.com");
        user4.setLogin("another");
        user4.setName("lastone");
        user4.setBirthday(LocalDate.of(1990, 5, 7));
    }

    @Test
    @DisplayName("Тест на добавление пустого запроса")
    void addEmptyUserTest() {
        User newUser = new User();
        response = getPostResponse(newUser);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на добавление нового юзера и получение ИД")
    void addUserTest() {
        User newUser = new User();
        newUser.setEmail("wassap@gmail.com");
        newUser.setLogin("wassapov");
        newUser.setName("Nikola");
        newUser.setBirthday(LocalDate.of(1988, 10, 11));

        response = getPostResponse(newUser);
        int id = response.getBody().getId();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), userDbStorage.getById(id));
    }

    @Test
    @DisplayName("Тест на получение юзера по ИД")
    void getUserTest() {
        response = getPostResponse(user1);
        int id = response.getBody().getId();
        response = restTemplate.getForEntity("/users/" + id, User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getId(), id);
        assertEquals(response.getBody(), userDbStorage.getById(id));
    }

    @Test
    @DisplayName("Тест на получение всех юзеров")
    void getAllTest() {
        response = getPostResponse(user1);
        response = getPostResponse(user2);

        ResponseEntity<User[]> responseList = restTemplate.getForEntity("/users/", User[].class);
        User[] users = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        List<User> usersInDb = userDbStorage.getAll();
        for (int i = 0; i < usersInDb.size(); i++) {
            assertEquals(users[i], usersInDb.get(i));
        }
    }

    @Test
    @DisplayName("Тест на обновление юзера")
    void updateFilmTest() {
        response = getPostResponse(user1);
        User beforeUpdateUser = response.getBody();

        beforeUpdateUser.setName("Konstantin");
        beforeUpdateUser.setLogin("Newlogin");
        response = getPutResponse(beforeUpdateUser);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        response = restTemplate.getForEntity("/users/" + beforeUpdateUser.getId(), User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), beforeUpdateUser);
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
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
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

        User testUser = userDbStorage.getById(userId);
        assertEquals(testUser.getFriends().toArray()[0], friendId);
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

        User testUser = userDbStorage.getById(userId);
        assertEquals(testUser.getFriends().toArray()[0], friendId);

        response = restTemplate.exchange("/users/" + userId + "/friends/" + friendId, HttpMethod.DELETE, entity, User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        testUser = userDbStorage.getById(userId);
        assertTrue(testUser.getFriends().isEmpty());
    }

    @Test
    @DisplayName("Тест на получение всех друзей")
    void getAllFriendsTest() {
        response = getPostResponse(user1);
        int userId = response.getBody().getId();

        response = getPostResponse(user2);
        User friend1 = response.getBody();
        response = getPostResponse(user3);
        User friend2 = response.getBody();
        User[] rightFriendsArr = new User[]{friend1, friend2};

        HttpEntity<User> entity = new HttpEntity<>(user1);
        restTemplate.exchange("/users/" + userId + "/friends/" + friend1.getId(), HttpMethod.PUT, entity, User.class);
        restTemplate.exchange("/users/" + userId + "/friends/" + friend2.getId(), HttpMethod.PUT, entity, User.class);

        ResponseEntity<User[]> responseList = restTemplate.getForEntity("/users/" + userId + "/friends", User[].class);
        User[] friends = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        for (int i = 0; i < rightFriendsArr.length; i++) {
            assertEquals(rightFriendsArr[i], friends[i]);
        }
    }

    @Test
    @DisplayName("Тест на получение общих друзей")
    void getCommonTest() {
        response = getPostResponse(user1);
        int userId1 = response.getBody().getId();
        response = getPostResponse(user2);
        int userId2 = response.getBody().getId();
        response = getPostResponse(user3);
        User commonFriend = response.getBody();
        response = getPostResponse(user4);
        User nonCommonFriend = response.getBody();

        HttpEntity<User> entity = new HttpEntity<>(user1);
        restTemplate.exchange("/users/" + userId1 + "/friends/" + commonFriend.getId(), HttpMethod.PUT, entity, User.class);
        restTemplate.exchange("/users/" + userId1 + "/friends/" + nonCommonFriend.getId(), HttpMethod.PUT, entity, User.class);

        ResponseEntity<User[]> responseList = restTemplate.getForEntity("/users/" + userId1 + "/friends/common/" + userId2, User[].class);
        User[] commonFriends = responseList.getBody();
        assertEquals(commonFriends.length, 0);

        entity = new HttpEntity<>(user2);
        restTemplate.exchange("/users/" + userId2 + "/friends/" + commonFriend.getId(), HttpMethod.PUT, entity, User.class);

        responseList = restTemplate.getForEntity("/users/" + userId1 + "/friends/common/" + userId2, User[].class);
        commonFriends = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);
        assertEquals(commonFriends.length, 1);
        assertEquals(commonFriends[0], commonFriend);
    }

    private ResponseEntity<User> getPostResponse(User user) {
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<User> getPutResponse(User user) {
        HttpEntity<User> entity = new HttpEntity<>(user);
        return restTemplate.exchange("/users", HttpMethod.PUT, entity, User.class, user.getId());
    }
}