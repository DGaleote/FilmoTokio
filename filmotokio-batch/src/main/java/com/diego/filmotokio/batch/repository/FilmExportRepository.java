package com.diego.filmotokio.batch.repository;

import com.diego.filmotokio.batch.model.FilmExport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmExportRepository extends JpaRepository<FilmExport, Long> {
}