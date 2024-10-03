package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.interfaces.AdvanceInfo;
import ru.yandex.practicum.filmorate.interfaces.BasicInfo;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film create(@RequestBody @Validated(BasicInfo.class) Film film) {
        log.info("Реквест на создание фильма: {}", film);
        validateFilmProductionData(film);
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody @Validated(AdvanceInfo.class) Film newFilm) {
        log.info("Реквест на обновление фильма: {}", newFilm);
        if (!filmStorage.containsFilm(newFilm.getId())) {
            log.error("Фильм с ID {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        validateFilmProductionData(newFilm);
        return filmStorage.updateFilm(newFilm);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Реквест на получение всех фильмов");
        return filmStorage.getAllFilms();
    }

    private void validateFilmProductionData(Film film) {
        log.info("Валидация данных фильма: {}", film);
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации: Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
