package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MpaDaoImplementation implements MpaDao{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getById(int id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from mpa_rating where mpa_rating_id =  ?", id);
        if (rs.next()) {
            return new Mpa(rs.getInt(1), rs.getString(2));
        }
        return null;
    }

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.queryForStream("select * from mpa_rating",
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_rating_id"), rs.getString("name"))).collect(Collectors.toList());
    }
}
