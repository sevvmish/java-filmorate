package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @Size(min = 0, max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    private Mpa mpa;

    @EqualsAndHashCode.Exclude
    private Set<Integer> likes = new HashSet<>();

    @EqualsAndHashCode.Exclude
    private List<Genre> genres = new ArrayList<>();
}
