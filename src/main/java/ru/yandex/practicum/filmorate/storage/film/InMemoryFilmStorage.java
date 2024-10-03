package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен с ID: {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        long filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            log.error("Фильм с ID {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        films.replace(filmId, newFilm);
        log.info("Фильм с ID {} обновлен", filmId);
        return newFilm;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public boolean containsFilm(long id) {
        return films.containsKey(id);
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
