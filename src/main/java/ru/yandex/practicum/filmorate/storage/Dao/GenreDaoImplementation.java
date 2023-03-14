package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDaoImplementation implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from genres where genre_id =  ?", id);
        if (rs.next()) {
            return new Genre(rs.getInt(1), rs.getString(2));
        }
        throw new ObjectNotFoundException("wrong id: no such Genre to return");
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.queryForStream("select * from genres",
                (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name"))).collect(Collectors.toList());
    }
}
