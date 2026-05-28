package com.diego.filmotokio.batch.service;

import com.diego.filmotokio.batch.model.FilmExport;
import com.diego.filmotokio.batch.repository.FilmExportRepository;
import com.diego.filmotokio.batch.repository.FilmRepository;
import com.diego.filmotokio.common.model.Film;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmExportService {

    private final FilmRepository filmRepository;
    private final FilmExportRepository filmExportRepository;

    public List<Film> findFilmsPendingExport() {
        return filmRepository.findFilmsPendingExport();
    }

    public void markFilmsAsExported(List<Film> films, Long jobId) {
        LocalDateTime now = LocalDateTime.now();

        List<FilmExport> exports = films.stream()
                .map(film -> {
                    FilmExport filmExport = new FilmExport();
                    filmExport.setFilmId(film.getId());
                    filmExport.setJobId(jobId);
                    filmExport.setExportedAt(now);
                    return filmExport;
                })
                .toList();

        filmExportRepository.saveAll(exports);
    }
}