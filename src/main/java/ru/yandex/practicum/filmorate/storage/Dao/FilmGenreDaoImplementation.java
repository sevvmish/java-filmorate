package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmGenreDaoImplementation implements FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void updateGenreByFilm(Film film) {
        if (film.getGenres().isEmpty()) {
            deleteAllInDb(film);
            return;
        }

        deleteAllInDb(film);
        List<Genre> genres = new ArrayList<>(film.getGenres());

        jdbcTemplate.batchUpdate("insert into film_genres(film_id, genre_id) values  (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, film.getId());
                        preparedStatement.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    private void deleteAllInDb(Film film) {
        final String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }
}
