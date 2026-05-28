package com.diego.filmotokio.web.service;

import com.diego.filmotokio.common.dto.FilmDetailDTO;
import com.diego.filmotokio.common.dto.FilmSummaryDTO;
import com.diego.filmotokio.common.model.Film;
import com.diego.filmotokio.web.repository.FilmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;

    public List<FilmSummaryDTO> getFilmSummaries() {
        return filmRepository.findFilmSummaries();
    }

    public List<FilmSummaryDTO> searchFilmSummariesByTitle(String title) {
        return filmRepository.findFilmSummariesByTitleContaining(title);
    }

    public FilmDetailDTO getFilmDetail(long id) {

        Film film = filmRepository.findById(id).orElseThrow();

        List<String> actors = film.getActors()
                .stream()
                .map(actor -> actor.getName() + " " + actor.getSurname())
                .toList();
        String director = film.getDirector().getName()
                + " "
                + film.getDirector().getSurname();


        return new FilmDetailDTO(
                film.getId(),
                film.getTitle(),
                film.getYear(),
                film.getSynopsis(),
                director,
                actors,
                film.getPoster()
        );
    }

    public Film findById(Long id) {
        return filmRepository.findById(id).orElseThrow();
    }

    public void save(Film film) {
        normalizeFilmTextFields(film);
        filmRepository.save(film);
    }

    private void normalizeFilmTextFields(Film film) {
        film.setTitle(stripOrNull(film.getTitle()));
        film.setSynopsis(stripOrNull(film.getSynopsis()));
    }

    private String stripOrNull(String value) {
        return value == null ? null : value.strip();
    }

    public void deleteById(Long id) {
        filmRepository.deleteById(id);
    }

}