package com.diego.filmotokio.rest.exception;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException(Long filmId, Long userId) {
        super("No existe valoración para la película " + filmId + " y el usuario " + userId);
    }
}