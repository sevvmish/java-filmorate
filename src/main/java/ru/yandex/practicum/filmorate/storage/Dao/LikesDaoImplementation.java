package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LikesDaoImplementation implements LikesDao{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getLikes(Film film) {
        final String sql = "select * from likes where film_id = ?";
        List<Integer> likes = jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getInt("user_id"), film.getId());
        return likes.stream().collect(Collectors.toSet());
    }

    @Override
    public void insertLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            return;
        }

        String sql = "insert into likes(user_id, film_id) values (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Integer like : film.getLikes()) {
                ps.setLong(1, like);
                ps.setLong(2, film.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            deleteLikes(film);
            return;
        }
        deleteLikes(film);
        insertLikes(film);
    }

    @Override
    public void deleteLikes(Film film) {
        String sql = "delete from likes where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }
}
