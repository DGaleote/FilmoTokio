package com.diego.filmotokio.batch.repository;

import com.diego.filmotokio.common.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {

    @Query("""
        select f
        from Film f
        where f.id > :lastId
          and f.id not in (
              select fe.filmId
              from FilmExport fe
          )
        order by f.id asc
        """)
    List<Film> findFilmsPendingExportAfterId(
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}