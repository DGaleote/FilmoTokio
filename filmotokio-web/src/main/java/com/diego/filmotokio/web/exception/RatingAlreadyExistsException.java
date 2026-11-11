package com.diego.filmotokio.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RatingAlreadyExistsException extends RuntimeException {

    public RatingAlreadyExistsException() {
        super("El usuario ya ha valorado esta película");
    }
}