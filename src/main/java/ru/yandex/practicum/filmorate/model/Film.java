package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    @NotNull(message = "Id должен быть указан", groups = AdvanceInfo.class)
    private Long id;
    @NotNull(message = "Название фильма не может быть пустым", groups = {BasicInfo.class, AdvanceInfo.class})
    @NotBlank(message = "Название фильма не может быть пустым", groups = {BasicInfo.class, AdvanceInfo.class})
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов", groups = {BasicInfo.class, AdvanceInfo.class})
    private String description;
    private LocalDate releaseDate;
    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом", groups = {BasicInfo.class, AdvanceInfo.class})
    private int duration;
}
