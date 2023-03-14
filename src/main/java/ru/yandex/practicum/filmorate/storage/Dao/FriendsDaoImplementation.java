package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDaoImplementation implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getFriends(Integer id) {
        String sql = "select * from users where user_id IN (select friend_id from friends where user_id = ?)";
        List<User> friends = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            User friend = new User();
            friend.setId(rs.getInt("user_id"));
            friend.setEmail(rs.getString("email"));
            friend.setLogin(rs.getString("login"));
            friend.setName(rs.getString("name"));
            friend.setBirthday(rs.getDate("birthday").toLocalDate());
            friends.add(friend);
        }
        return friends;
    }

    @Override
    public void insertFriends(Integer id, Integer friendId) {

        String sql = "insert into friends (user_id, friend_id) values (?, ?)";

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, friendId);
            ps.addBatch();

            ps.executeBatch();
        } catch (SQLException e) {
            log.error("error while trying insertFriend");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        String sql = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
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
