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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private final LikesDao likesDao;

    @Override
    public List<Film> getAll() {
        List<Film> films = new ArrayList<>();

        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                    "select FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE," +
                            " FILMS.DURATION, FILMS.mpa_rating_id, mpa_rating.NAME as MPAA_NAME from films " +
                            "join mpa_rating on FILMS.mpa_rating_id = mpa_rating.mpa_rating_id " +
                            "order by FILM_ID"
            );
            while (rs.next()) {

                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setMpa(new Mpa(rs.getInt("mpa_rating_id"),
                        rs.getString("MPAA_NAME")));
                film.setGenres(getGenresByFilmId(film.getId()));
                films.add(film);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }


        return films;
    }

    @Override
    public Film add(Film film) {
        if (!isValidationChecked(film)) {
            throw new ValidationException("data validation error");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(this.filmToMap(film)).intValue());

        filmGenreDao.updateGenreByFilm(film);
        likesDao.updateLikes(film);
        return film;
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        if (film.getMpa() != null) {
            values.put("mpa_rating_id", film.getMpa().getId());
        }

        return values;
    }

    @Override
    public Film update(Film film) {
        if (!isValidationChecked(film) || film.getId() == null || film.getId() < 1) {
            throw new ValidationException("validation exception");
        }

        final String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?   " +
                "where film_id = ?";
        int count = jdbcTemplate.update(sql
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());

        if (count == 1) {

            filmGenreDao.updateGenreByFilm(film);
            likesDao.updateLikes(film);
            return film;
        } else {
            throw new ObjectNotFoundException("wrong id: no such film to update");
        }
    }

    @Override
    public Film getById(Integer id) {
        String sql = "select count(*) from films where film_id=?";
        if (jdbcTemplate.queryForObject(sql, Integer.class, id) == 0) {
            throw new ObjectNotFoundException("No such film found");
        }

        Film film = new Film();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "select films.film_id as film_id, " +
                        "films.name , films.description, films.release_date, films.duration, films.mpa_rating_id, " +
                        "mpa_rating.name as MPA, film_genres.genre_id, " +
                        "genres.name as GN, " +
                        "likes.user_id from films " +
                        "left join film_genres on films.film_id = film_genres.film_id " +
                        "left join genres on film_genres.genre_id = genres.genre_id " +
                        "left join likes on films.film_id = likes.film_id " +
                        "left join mpa_rating on films.mpa_rating_id = mpa_rating.mpa_rating_id " +
                        "where films.film_id = ?",
                id);


        if (rs.next()) {
            Set<Genre> genres = new HashSet<>();
            Set<Integer> likes = new HashSet<>();

            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Mpa(rs.getInt("mpa_rating_id"),
                    rs.getString("MPA")));

            do {
                if (rs.getString("GN") != null) {
                    genres.add(new Genre(rs.getInt("genre_id"), rs.getString("GN")));
                }

                int i = rs.getInt("user_id");
                if (i != 0 && !likes.contains(i)) {
                    likes.add(i);
                }
            } while (rs.next());
            film.setGenres(genres.stream().collect(Collectors.toList()));
            film.setLikes(likes);
            return film;
        }

        return film;
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        List<Film> films = new ArrayList<>();

        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                    "select films.film_id as film_id, films.name , films.description, films.release_date, films.duration, " +
                            "films.mpa_rating_id, mpa_rating.name as MPA, COUNT(likes.film_id) as all_likes " +
                            "from films " +
                            "left join likes on films.film_id = likes.film_id " +
                            "join mpa_rating on films.mpa_rating_id = mpa_rating.mpa_rating_id " +
                            "GROUP BY films.film_id " +
                            "ORDER BY all_likes DESC " +
                            "LIMIT ?", count);

            while (rs.next()) {

                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setMpa(new Mpa(rs.getInt("mpa_rating_id"), rs.getString("MPA")));
                films.add(film);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return films;
    }

    private boolean isValidationChecked(Film film) {
        if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            log.warn("release date is wrong");
            return false;
        }
        return true;
    }

    private List<Genre> getGenresByFilmId(Integer id) {
        Film film = getById(id);
        return film.getGenres();
    }
}
