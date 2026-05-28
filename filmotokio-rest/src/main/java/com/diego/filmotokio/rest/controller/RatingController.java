package com.diego.filmotokio.rest.controller;

import com.diego.filmotokio.rest.dto.CreateRatingRequest;
import com.diego.filmotokio.rest.dto.RatingAverageResponse;
import com.diego.filmotokio.rest.dto.RatingResponse;
import com.diego.filmotokio.rest.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRating(@Valid @RequestBody CreateRatingRequest request) {
        ratingService.createRating(request);
    }

    @GetMapping("/ratings/films/{filmId}/users/{userId}")
    public RatingResponse getRatingByFilmAndUser(
            @PathVariable("filmId") Long filmId,
            @PathVariable("userId") Long userId) {

        return ratingService.getRatingByFilmAndUser(filmId, userId);
    }

    @GetMapping("/ratings-average/films/{filmId}")
    public RatingAverageResponse getAverageByFilm(@PathVariable("filmId") Long filmId) {
        return ratingService.getAverageByFilm(filmId);
    }
}