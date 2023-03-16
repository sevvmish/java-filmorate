package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
