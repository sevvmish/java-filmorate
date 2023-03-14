package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDaoImplementation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService implements GenreDao {
    private final GenreDaoImplementation genreDao;

    @Override
    public Genre getById(Integer id) {
        return genreDao.getById(id);
    }

    public List<Genre> getAll() {
        return genreDao.getAll();
    }
}
