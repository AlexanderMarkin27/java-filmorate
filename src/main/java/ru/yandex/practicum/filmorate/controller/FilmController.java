package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 0;

    @PostMapping
    public Film create(@RequestBody @Validated(BasicInfo.class) Film film) {
        log.info("Реквест на создание фильма: {}", film);
        validateFilmProductionData(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан фильм с ID: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Validated(AdvanceInfo.class) Film newFilm) {
        log.info("Реквест на обновление фильма: {}", newFilm);
        long filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            log.error("Фильм с ID {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        validateFilmProductionData(newFilm);
        films.replace(filmId, newFilm);
        log.info("Фильм с ID ID {} обновлен", filmId);
        return newFilm;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Реквест на получение всех фильмов");
        return films.values();
    }

    private long getNextId() {
        return ++currentMaxId;
    }

    private void validateFilmProductionData(Film film) {
        log.info("Валидация данных фильма: {}", film);
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации: Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

}
