package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User user1;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();

        user1 = new User();
        user1.setEmail("ivanov@gmail.com");
        user1.setLogin("ivanov");
        user1.setName("Ivan");
        user1.setBirthday(LocalDate.of(1988, 10,11));

        user2 = new User();
        user2.setEmail("petrov@gmail.com");
        user2.setLogin("petrov");
        user2.setName("Petr");
        user2.setBirthday(LocalDate.of(1982, 5,11));
    }

    @Test
    @DisplayName("Тест на добавление пустого запроса")
    void addEmptyUserTest() {
        User newUser = new User();
        assertThrows(NullPointerException.class, ()->userController.add(newUser));
    }

    @Test
    @DisplayName("Тест на добавление юзера и получения ИД")
    void addUserTest() {
        User newUser = userController.add(user1);
        assertEquals(1, newUser.getId());
    }

    @Test
    @DisplayName("Тест на ошибку при добавлении логина: нулевой, пустой, с пробелом")
    void loginErrorTest() {
        user1.setLogin(null);
        assertThrows(NullPointerException.class, ()->userController.add(user1));

        user1.setLogin("");
        assertThrows(ValidationException.class, ()->userController.add(user1));

        user2.setLogin("great man");
        assertThrows(ValidationException.class, ()->userController.add(user2));
    }

    @Test
    @DisplayName("Тест на ошибку в имейле: нулевой, пустой, без @")
    void emailErrorTest() {
        user1.setEmail(null);
        assertThrows(NullPointerException.class, ()->userController.add(user1));

        user1.setEmail("");
        assertThrows(ValidationException.class, ()->userController.add(user1));

        user2.setEmail("great.man");
        assertThrows(ValidationException.class, ()->userController.add(user2));
    }

    @Test
    @DisplayName("Тест на не существующий день рождения")
    void birthdayErrorTest() {
        user1.setBirthday(LocalDate.of(2024,10,7));
        assertThrows(ValidationException.class, ()->userController.add(user1));
    }


    @Test
    @DisplayName("Тест на ошибку при обновлении юзера, которого не было ранее по его ИД")
    void updateWrongUserErrorTest() {
        userController.add(user1);
        assertThrows(ValidationException.class, ()->userController.update(user2));
    }

    @Test
    @DisplayName("Тест на ошибку валидации при обновлении юзера на новые данные")
    void updateFilmWithNewBadDataErrorTest() {
        userController.add(user1);
        user2.setId(1);
        user2.setEmail("best-email");
        assertThrows(ValidationException.class, ()->userController.update(user2));
    }
}