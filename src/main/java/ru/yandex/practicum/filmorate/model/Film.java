package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    @Size(min = 0, max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;

    @NotNull
    @Valid
    private Mpa mpa;

    @EqualsAndHashCode.Exclude
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
}
