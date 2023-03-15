package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
    public void addLike(Integer filmId, Integer userId) {
        String sql = "insert into likes(user_id, film_id) values (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }


    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}
