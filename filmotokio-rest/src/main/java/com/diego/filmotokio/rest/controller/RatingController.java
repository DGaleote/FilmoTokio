package com.diego.filmotokio.rest.controller;

import com.diego.filmotokio.rest.dto.CreateRatingRequest;
import com.diego.filmotokio.rest.dto.RatingAverageResponse;
import com.diego.filmotokio.rest.dto.RatingResponse;
import com.diego.filmotokio.rest.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
public class RatingController {
    private static final Logger log = LoggerFactory.getLogger(RatingController.class);
    private final RatingService ratingService;

    @PostMapping("/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRating(@Valid @RequestBody CreateRatingRequest request) {

        log.info("Creating rating. Film id: {}, user id: {}, score: {}",
                request.getFilmId(),
                request.getUserId(),
                request.getScore());

        ratingService.createRating(request);

        log.info("Rating created successfully. Film id: {}, user id: {}, score: {}",
                request.getFilmId(),
                request.getUserId(),
                request.getScore());
    }

    @GetMapping("/ratings/films/{filmId}/users/{userId}")
    public RatingResponse getRatingByFilmAndUser(
            @PathVariable("filmId") Long filmId,
            @PathVariable("userId") Long userId) {

        log.info("Loading rating by film and user. Film id: {}, user id: {}",
                filmId,
                userId);

        RatingResponse response = ratingService.getRatingByFilmAndUser(filmId, userId);

        log.info("Rating found. Film id: {}, user id: {}, score: {}",
                filmId,
                userId,
                response.getScore());

        return response;
    }

    @GetMapping("/ratings-average/films/{filmId}")
    public RatingAverageResponse getAverageByFilm(@PathVariable("filmId") Long filmId) {

        log.info("Loading rating average. Film id: {}", filmId);

        RatingAverageResponse response = ratingService.getAverageByFilm(filmId);

        log.info("Rating average loaded. Film id: {}, average: {}, ratings: {}",
                filmId,
                response.getAverage(),
                response.getRatings());

        return response;
    }
}