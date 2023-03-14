DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa_rating;



CREATE TABLE IF NOT EXISTS mpa_rating
(
    mpa_rating_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255)
);

CREATE UNIQUE INDEX IF NOT EXISTS mpa_rating_mpa_rating_id_uindex
    ON mpa_rating (mpa_rating_id);


CREATE TABLE IF NOT EXISTS films
(
    film_id      INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255),
    description  VARCHAR(200),
    release_date DATE,
    duration     INT,
    mpa_rating_id      INT,
    CONSTRAINT fk_mpa_rating FOREIGN KEY (mpa_rating_id) REFERENCES mpa_rating (mpa_rating_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS films_film_id_uindex
    ON films (film_id);


CREATE TABLE IF NOT EXISTS users
(
    user_id  INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(255) NOT NULL,
    name     VARCHAR(255),
    birthday DATE
);

CREATE UNIQUE INDEX IF NOT EXISTS users_user_id_uindex ON users (user_id);
CREATE UNIQUE index if NOT EXISTS USER_EMAIL_UINDEX on users (email);
CREATE UNIQUE index if NOT EXISTS USER_LOGIN_UINDEX on users (login);



CREATE TABLE IF NOT EXISTS likes
(
    user_id INT,
    film_id INT,
    CONSTRAINT likes_pk PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_films FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name     VARCHAR(255)                          NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS genres_genre_id_uindex
    ON genres (genre_id);


CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INT NOT NULL,
    genre_id INT    NOT NULL,
    CONSTRAINT film_genres_pk PRIMARY KEY (film_id, genre_id),
    CONSTRAINT film_genres_fk_1 FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE CASCADE ,
    CONSTRAINT film_genres_fk_2 FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS friends
(
    user_id   INT NOT NULL,
    friend_id INT NOT NULL,
    CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id),
    CONSTRAINT friends_fk_1 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT friends_fk_2 FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);