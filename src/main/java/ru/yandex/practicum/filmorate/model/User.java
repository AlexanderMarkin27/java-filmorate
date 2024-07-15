package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {
    @NotNull(message = "Id должен быть указан", groups = AdvanceInfo.class)
    private Long id;
    @NotNull(message = "Электронная почта не может быть пустой", groups = {BasicInfo.class, AdvanceInfo.class})
    @Email(message = "Неправильный формат электронного адреса", groups = {BasicInfo.class, AdvanceInfo.class})
    private String email;
    @NotNull(message = "Логин не может быть пустым", groups = {BasicInfo.class, AdvanceInfo.class})
    @NotBlank(message = "Логин не может содержать пробелы", groups = {BasicInfo.class, AdvanceInfo.class})
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем", groups = {BasicInfo.class, AdvanceInfo.class})
    private LocalDate birthday;
}
