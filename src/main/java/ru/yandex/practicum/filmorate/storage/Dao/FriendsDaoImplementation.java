package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDaoImplementation implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getFriends(Integer id) {
        String sql = "select * from USERS, FRIENDS where USERS.USER_ID = FRIENDS.FRIEND_ID AND FRIENDS.USER_ID = ?";
        List<User> friends = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            friends.add(convertSqlRowSetToUser(rs));
        }
        return friends;
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {

        String sql = "insert into friends (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        String sql = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public List<User> getCommon(Integer id, Integer otherId) {

        String sql = "select * from USERS u, FRIENDS f, FRIENDS o where u.USER_ID = f.FRIEND_ID " +
                "AND u.USER_ID = o.FRIEND_ID AND f.USER_ID = ? AND o.USER_ID = ?";

        List<User> users = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id, otherId);
        while (rs.next()) {
            users.add(convertSqlRowSetToUser(rs));
        }
        return users;
    }

    private User convertSqlRowSetToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }
}
