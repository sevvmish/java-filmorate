package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {
        List<Film> films = new ArrayList<>();
        Map<Integer, Film> filmsMap = new HashMap<>();
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                    "select FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE," +
                            " FILMS.DURATION, FILMS.mpa_rating_id, mpa_rating.NAME as MPA from films " +
                            "join mpa_rating on FILMS.mpa_rating_id = mpa_rating.mpa_rating_id " +
                            "order by FILM_ID"
            );
            while (rs.next()) {
                Film film = convertSqlRowSetToFilm(rs);
                filmsMap.put(film.getId(), film);
                films.add(film);
            }
        } catch (Exception ex) {
            log.error("error getting all films");
            ex.printStackTrace();
        }

        if (!filmsMap.isEmpty()) {
            return getGenresByFilmId(filmsMap);
        }

        return films;
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        film.setId(simpleJdbcInsert.executeAndReturnKey(this.filmToMap(film)).intValue());

        return film;
    }

    @Override
    public Film update(Film film) {
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

            return film;
        } else {
            throw new ObjectNotFoundException("wrong id: no such film to update");
        }
    }

    @Override
    public Film getById(Integer id) {
        Film film = new Film();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "select films.film_id as film_id, " +
                        "films.name , films.description, films.release_date, films.duration, films.mpa_rating_id, " +
                        "mpa_rating.name as MPA, film_genres.genre_id, " +
                        "genres.name as GN " +
                        "from films " +
                        "left join film_genres on films.film_id = film_genres.film_id " +
                        "left join genres on film_genres.genre_id = genres.genre_id " +
                        "left join mpa_rating on films.mpa_rating_id = mpa_rating.mpa_rating_id " +
                        "where films.film_id = ?",
                id);


        if (rs.next()) {
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            film = convertSqlRowSetToFilm(rs);
            do {
                if (rs.getString("GN") != null) {
                    genres.add(new Genre(rs.getInt("genre_id"), rs.getString("GN")));
                }
            } while (rs.next());
            film.setGenres(genres);
        } else {
            throw new ObjectNotFoundException("No such film found");
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
                films.add(convertSqlRowSetToFilm(rs));
            }
        } catch (Exception ex) {
            log.error("error while trying getMostPopular list");
            ex.printStackTrace();
        }

        return films;
    }

    private List<Film> getGenresByFilmId(Map<Integer, Film> filmsMap) {
        List<Integer> filmIds = new ArrayList<>(filmsMap.keySet());

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        try {
            jdbcTemplate.query(
                    String.format("SELECT FILM_GENRES.FILM_ID,FILM_GENRES.GENRE_ID, GENRES.NAME " +
                            "FROM FILM_GENRES " +
                            "JOIN GENRES ON  FILM_GENRES.GENRE_ID = GENRES.GENRE_ID " +
                            "WHERE FILM_GENRES.FILM_ID IN (%s)", inSql),
                    filmIds.toArray(),
                    (rs, rowNum) -> filmsMap.get(rs.getInt("FILM_ID")).getGenres().
                            add(new Genre(rs.getInt("GENRE_ID"), rs.getString("NAME"))));

        } catch (Exception ex) {
            log.error("error trying get genres for getall films");
            ex.printStackTrace();
        }

        return filmsMap.values().stream().collect(Collectors.toList());
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

    private Film convertSqlRowSetToFilm(SqlRowSet rs) {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Mpa(rs.getInt("mpa_rating_id"), rs.getString("MPA")));

        return film;
    }
}
