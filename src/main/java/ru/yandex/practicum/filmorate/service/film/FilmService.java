package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);

        filmLikes.putIfAbsent(filmId, new HashSet<>());

        if (filmLikes.get(filmId).contains(userId)) {
            log.info("Пользователь с ID {} уже поставил лайк фильму с ID {}", userId, filmId);
        } else {
            filmLikes.get(filmId).add(userId);
            log.info("Лайк от пользователя с ID {} добавлен фильму с ID {}", userId, filmId);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);

        if (filmLikes.containsKey(filmId) && filmLikes.get(filmId).contains(userId)) {
            filmLikes.get(filmId).remove(userId);
            log.info("Лайк от пользователя с ID {} удален у фильма с ID {}", userId, filmId);
        } else {
            log.error("Попытка удалить лайк от пользователя с ID {} у фильма с ID {}, но лайк не найден", userId, filmId);
            throw new NotFoundException("Лайк от пользователя с ID " + userId + " не найден у фильма с ID " + filmId);
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmLikes.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))  // Сортировка по количеству лайков
                .limit(count)
                .map(entry -> getFilmById(entry.getKey()))
                .collect(Collectors.toList());
    }

    private Film getFilmById(long filmId) {
        if (!filmStorage.containsFilm(filmId)) {
            log.error("Фильм с ID {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return  filmStorage.getAllFilms().stream()
                .filter(film -> film.getId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));

    }
}

