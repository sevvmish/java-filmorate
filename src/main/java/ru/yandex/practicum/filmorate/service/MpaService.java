package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Dao.MpaDaoImplementation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDaoImplementation mpaDao;

    public Mpa getById(Integer id) {
        return mpaDao.getById(id);
    }

    public List<Mpa> getAll() {
        return mpaDao.getAll();
    }
}
