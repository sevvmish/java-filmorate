package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

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
}
