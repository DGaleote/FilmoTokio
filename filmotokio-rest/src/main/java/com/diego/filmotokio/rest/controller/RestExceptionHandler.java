package com.diego.filmotokio.rest.controller;

import com.diego.filmotokio.rest.exception.RatingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {

        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(RatingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRatingNotFoundException(RatingNotFoundException e) {
        return Map.of("error", e.getMessage());
    }
}