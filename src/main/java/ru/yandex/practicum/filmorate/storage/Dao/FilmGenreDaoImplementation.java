package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmGenreDaoImplementation implements FilmGenreDao{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Genre> findAllByFilmId(Integer id) {
        Set<Genre> genres = new HashSet<>();
        String sql = "select * from film_genres join genres on genres.genre_id = film_genres.genre_id " +
                "where film_id = ? order by 1 asc ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);
        while (rs.next()) {
            genres.add(new Genre(rs.getInt("genre_id"),
                    rs.getString("name")));
        }
        return genres;
    }

    @Override
    public void addGenreToFilm(Integer filmId, Genre genre) {
        String sql = "insert into film_genres(film_id, genre_id) values  (?, ?)";
        jdbcTemplate.update(sql, filmId, genre.getId());
    }

    @Override
    public void updateGenreByFilm(Film film) {
        if (film.getGenres().isEmpty()) {
            deleteAllInDb(film);
            return;
        }

        deleteAllInDb(film);
        film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));

        for (Genre genreToAdd : film.getGenres()) {
            addGenreToFilm(film.getId(), genreToAdd);
        }
    }

    private void deleteAllInDb(Film film) {
        final String sql = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }
}
