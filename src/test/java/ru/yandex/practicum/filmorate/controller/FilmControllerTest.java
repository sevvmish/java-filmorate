package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;
    private Film film1;
    private Film film2;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();

        film1 = new Film();
        film1.setName("cats-killers");
        film1.setDescription("description of horror cats");
        film1.setReleaseDate(LocalDate.of(1950, 11,11));
        film1.setDuration(60);

        film2 = new Film();
        film2.setName("cats-savers");
        film2.setDescription("description of holy cats");
        film2.setReleaseDate(LocalDate.of(2001, 11,11));
        film2.setDuration(42);
    }

    @Test
    @DisplayName("Тест на добавление пустого запроса")
    void addEmptyFilmTest() {
        Film newFilm = new Film();
        assertThrows(NullPointerException.class, ()->filmController.add(newFilm));
    }

    @Test
    @DisplayName("Тест на добавление фильма и получения ИД")
    void addFilmTest() {
        Film newFilm = filmController.add(film1);
        assertEquals(1, newFilm.getId());
    }

    @Test
    @DisplayName("Тест на ошибку при добавлении фильма с нулевым или пустым именем")
    void nullOrEmptyNameErrorTest() {
        film1.setName(null);
        assertThrows(NullPointerException.class, ()->filmController.add(film1));

        film2.setName("");
        assertThrows(ValidationException.class, ()->filmController.add(film2));
    }

    @Test
    @DisplayName("Тест на ошибку из-за описания длинее 200 символов")
    void longDescriptionErrorTest() {
        String newDescription = film1.getDescription().repeat(200);
        film1.setDescription(newDescription);
        assertThrows(ValidationException.class, ()->filmController.add(film1));
    }

    @Test
    @DisplayName("Тест на ошибку в дате релиза")
    void releaseDateErrorTest() {
        film1.setReleaseDate(LocalDate.of(1890,10,10));
        assertThrows(ValidationException.class, ()->filmController.add(film1));
    }

    @Test
    @DisplayName("Тест на ошибку в указании длительности фильма")
    void durationErrorTest() {
        film1.setDuration(-1);
        assertThrows(ValidationException.class, ()->filmController.add(film1));
    }

    @Test
    @DisplayName("Тест на ошибку при обновлении фильма, которого не было ранее по его ИД")
    void updateWrongFilmErrorTest() {
        filmController.add(film1);
        assertThrows(ValidationException.class, ()->filmController.update(film2));
    }

    @Test
    @DisplayName("Тест на ошибку валидации при обновлении фильма на новые данные")
    void updateFilmWithNewBadDataErrorTest() {
        filmController.add(film1);
        film2.setId(1);
        film2.setDuration(-2);
        assertThrows(ValidationException.class, ()->filmController.update(film2));
    }

}