package com.diego.filmotokio.rest.service;

import com.diego.filmotokio.rest.dto.CreateRatingRequest;
import com.diego.filmotokio.rest.dto.RatingAverageResponse;
import com.diego.filmotokio.rest.dto.RatingResponse;
import com.diego.filmotokio.rest.exception.RatingNotFoundException;
import com.diego.filmotokio.rest.model.Rating;
import com.diego.filmotokio.rest.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public void createRating(CreateRatingRequest request) {

        if (ratingRepository.existsByUserIdAndFilmId(
                request.getUserId(),
                request.getFilmId())) {

            throw new IllegalArgumentException("El usuario ya ha valorado esta película");
        }

        Rating rating = new Rating();
        rating.setUserId(request.getUserId());
        rating.setFilmId(request.getFilmId());
        rating.setScore(request.getScore());
        rating.setCreatedAt(LocalDateTime.now());

        ratingRepository.save(rating);
    }

    public RatingResponse getRatingByFilmAndUser(Long filmId, Long userId) {

        Rating rating = ratingRepository.findByFilmIdAndUserId(filmId, userId)
                .orElseThrow(() -> new RatingNotFoundException(filmId, userId));

        return new RatingResponse(
                rating.getScore(),
                rating.getCreatedAt()
        );
    }

    public RatingAverageResponse getAverageByFilm(Long filmId) {

        List<Rating> ratings = ratingRepository.findByFilmId(filmId);

        if (ratings.isEmpty()) {
            return new RatingAverageResponse(BigDecimal.ZERO, 0);
        }

        double average = ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0);

        BigDecimal roundedAverage = BigDecimal.valueOf(average)
                .setScale(2, RoundingMode.CEILING);

        return new RatingAverageResponse(
                roundedAverage,
                ratings.size()
        );
    }
}