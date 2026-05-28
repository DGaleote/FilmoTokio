package com.diego.filmotokio.web.controller;

import com.diego.filmotokio.common.dto.FilmDetailDTO;
import com.diego.filmotokio.common.dto.FilmSummaryDTO;
import com.diego.filmotokio.web.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public List<FilmSummaryDTO> getFilms() {
        return filmService.getFilmSummaries();
    }

    @GetMapping("/films/{id}")
    public FilmDetailDTO getFilmDetail(@PathVariable("id") Long id) {
        return filmService.getFilmDetail(id);
    }

}