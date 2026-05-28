package com.diego.filmotokio.web.repository;

import com.diego.filmotokio.common.model.Artist;
import com.diego.filmotokio.common.model.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByType(ArtistType type);

}