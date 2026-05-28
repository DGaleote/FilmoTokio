package com.diego.filmotokio.rest.repository;

import com.diego.filmotokio.rest.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByUserIdAndFilmId(Long userId, Long filmId);

    Optional<Rating> findByFilmIdAndUserId(Long filmId, Long userId);

    List<Rating> findByFilmId(Long filmId);
}