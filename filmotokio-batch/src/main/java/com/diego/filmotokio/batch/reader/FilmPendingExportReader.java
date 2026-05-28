package com.diego.filmotokio.batch.reader;

import com.diego.filmotokio.batch.service.FilmExportService;
import com.diego.filmotokio.common.model.Film;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmPendingExportReader implements ItemReader<Film> {

    private final FilmExportService filmExportService;

    private Iterator<Film> filmIterator;

    @Override
    public Film read() {
        if (filmIterator == null) {
            List<Film> pendingFilms = filmExportService.findFilmsPendingExport();
            filmIterator = pendingFilms.iterator();
        }

        if (filmIterator.hasNext()) {
            return filmIterator.next();
        }

        return null;
    }
}