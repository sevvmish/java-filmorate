package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        String sql = "select * from users";

        List<User> users = new ArrayList<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            users.add(convertSqlRowSetToUser(rs));
        }
        return users;
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(this.userToMap(user)).intValue());
        return user;
    }


    @Override
    public User update(User user) {
        if (user.getId() < 1) {
            throw new ObjectNotFoundException("wrong id: no such user to update");
        }

        final String sql = "update users set email = ?, login = ?, name = ?, birthday = ?   where user_id = ?";
        int count = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        if (count == 1) {
            return user;
        } else {
            throw new ObjectNotFoundException("wrong id: no such user to update");
        }
    }

    @Override
    public User getById(Integer id) {
        User user = new User();
        String sql = "select * from users where user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (rs.next()) {
            user = convertSqlRowSetToUser(rs);
        } else {
            throw new ObjectNotFoundException("No such user found");
        }

        return user;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        return values;
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
