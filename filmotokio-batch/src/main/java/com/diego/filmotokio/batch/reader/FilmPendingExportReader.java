package com.diego.filmotokio.batch.reader;

import com.diego.filmotokio.batch.service.FilmExportService;
import com.diego.filmotokio.common.model.Film;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class FilmPendingExportReader implements ItemReader<Film> {

    private final FilmExportService filmExportService;

    @Value("${filmotokio.batch.page-size}")
    private int pageSize;

    private Iterator<Film> filmIterator;
    private Long lastId = 0L;

    @Override
    public Film read() {

        if (filmIterator == null || !filmIterator.hasNext()) {
            List<Film> pendingFilms =
                    filmExportService.findFilmsPendingExport(lastId, pageSize);

            if (pendingFilms.isEmpty()) {
                return null;
            }

            filmIterator = pendingFilms.iterator();
        }

        Film film = filmIterator.next();
        lastId = film.getId();

        return film;
    }
}