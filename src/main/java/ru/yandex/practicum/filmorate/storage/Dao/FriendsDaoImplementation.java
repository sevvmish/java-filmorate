package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FriendsDaoImplementation implements FriendsDao{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getFriends(Integer id) {
        String sql = "select count(user_id) from users where user_id=?";
        if (jdbcTemplate.queryForObject(sql, Integer.class, id) == 0) {
            throw new ObjectNotFoundException("No such user");
        }

        sql = "select * from users where user_id IN (select friend_id from friends where user_id = ?)";
        Set<Integer> friends = new HashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            User friend = new User();
            friends.add(rs.getInt("user_id"));
        }
        return friends;
    }

    @Override
    public void insertFriends(User user) {
        if (user.getFriends().isEmpty()) {
            return;
        }
        String sql = "insert into friends (user_id, friend_id) values (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Integer friend : user.getFriends()) {
                ps.setInt(1, user.getId());
                ps.setInt(2, friend);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFriends(User user) {
        String sql = "delete from friends where user_id = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    @Override
    public List<User> getCommon(Integer id, Integer otherId) {
        String sql = "select * from users where user_id IN (select friend_id " +
                "from friends where user_id = " + id + ") " +
                "and user_id in (select friend_id from friends where user_id = " + otherId + ")";

        List<User> users = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            users.add(user);
        }
        return users;
    }
}
