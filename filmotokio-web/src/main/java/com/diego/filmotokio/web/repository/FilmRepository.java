package com.diego.filmotokio.web.repository;

import com.diego.filmotokio.common.dto.FilmSummaryDTO;
import com.diego.filmotokio.common.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {

    @Query("""
    SELECT new com.diego.filmotokio.common.dto.FilmSummaryDTO(f.id, f.title, f.year, f.poster)
    FROM Film f
    """)
    List<FilmSummaryDTO> findFilmSummaries();


    List<Film> findByTitleContainingIgnoreCase(String title);

    @Query("""
       select new com.diego.filmotokio.common.dto.FilmSummaryDTO(f.id, f.title, f.year, f.poster)
       from Film f
       where lower(f.title) like lower(concat('%', :title, '%'))
       """)
    List<FilmSummaryDTO> findFilmSummariesByTitleContaining(@Param("title") String title);


}