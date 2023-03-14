package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LikesDaoImplementation implements LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getLikes(Integer id) {
        final String sql = "select * from likes where film_id = ?";
        List<Integer> likes = jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getInt("user_id"), id);
        return likes.stream().collect(Collectors.toSet());
    }

    @Override
    public void insertLikes(Integer filmId, Integer userId) {

        String sql = "insert into likes(user_id, film_id) values (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ps.addBatch();

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deleteLikes(Integer filmId, Integer userId) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}
