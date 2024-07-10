package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.constraints.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody @NotNull Film film) {
        log.info("Реквест на создание фильма: {}", film);
        validateFilmData(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан фильм с ID: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Реквест на обновление фильма: {}", newFilm);
        if (newFilm == null || newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        Film existingFilm = films.get(newFilm.getId());
        if (existingFilm == null) {
            log.error("Фильм с ID {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        validateFilmData(newFilm);
        updateFilmData(existingFilm, newFilm);
        log.info("Фильм с ID ID {} обновлен", newFilm.getId());
        return existingFilm;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Реквест на получение всех фильмов");
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilmData(Film film) {
        log.info("Валидация данных фильма: {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации: Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации: Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private void updateFilmData(Film existingFilm, Film newFilm) {
        log.info("Обновление данных фильма с ID: {}", existingFilm.getId());
        existingFilm.setName(newFilm.getName());
        existingFilm.setDescription(newFilm.getDescription());
        existingFilm.setReleaseDate(newFilm.getReleaseDate());
        existingFilm.setDuration(newFilm.getDuration());
    }
}
