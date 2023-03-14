package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsDao friendsDao;

    @Override
    public List<User> getAll() {
        String sql = "select * from users";

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

    @Override
    public User add(User user) {
        if (!isValidationChecked(user)) {
            throw new ValidationException("data validation error");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        user.setId(simpleJdbcInsert.executeAndReturnKey(this.userToMap(user)).intValue());
        friendsDao.insertFriends(user);
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

    @Override
    public User update(User user) {
        if (!isValidationChecked(user) || user.getId() == null || user.getId() < 1) {
            throw new ValidationException("data validation error");
        }

        final String sql = "update users set email = ?, login = ?, name = ?, birthday = ?   where user_id = ?";
        int count = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        if (count == 1) {
            friendsDao.deleteFriends(user);
            friendsDao.insertFriends(user);
            return user;
        } else {
            throw new ObjectNotFoundException("wrong id: no such user to update");
        }
    }

    @Override
    public User getById(Integer id) {
        String sql = "select count(*) from users where user_id=?";
        if (jdbcTemplate.queryForObject(sql, Integer.class, id) == 0) {
            throw new ObjectNotFoundException("No such user found");
        }

        User user = new User();
        sql = "select * from users where user_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        if (rs.next()) {
            user.setId(rs.getInt("USER_ID"));
            user.setEmail(rs.getString("EMAIL"));
            user.setLogin(rs.getString("LOGIN"));
            user.setName(rs.getString("NAME"));
            user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
            user.setFriends(friendsDao.getFriends(user.getId()));
            return user;
        }

        return user;
    }

    private boolean isValidationChecked(User user) {
        if (!user.getLogin().isEmpty() && !user.getLogin().isBlank()) {
            for (int i = 0; i < user.getLogin().length(); i++) {
                if (Character.isWhitespace(user.getLogin().charAt(i))) {
                    log.warn("whitespace in login");
                    return false;
                }
            }
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return true;
    }

}
