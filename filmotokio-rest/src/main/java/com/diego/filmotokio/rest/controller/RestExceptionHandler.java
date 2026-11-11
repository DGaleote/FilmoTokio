package com.diego.filmotokio.rest.controller;

import com.diego.filmotokio.rest.exception.RatingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.stream.Collectors;
import java.util.Map;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class RestExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException e) {

        log.warn("REST bad request. Reason: {}", e.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage()
        );
    }

    @ExceptionHandler(RatingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleRatingNotFoundException(RatingNotFoundException e) {

        log.warn("REST rating not found. Reason: {}", e.getMessage());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("REST validation failed. Reason: {}", message);

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                message
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Map<String, Object> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException e) {

        log.warn("REST unsupported media type. Content type: {}",
                e.getContentType());

        return buildErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported Media Type",
                "El formato de contenido enviado no está soportado"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNoResourceFound(
            NoResourceFoundException e) {

        log.warn("REST resource not found. Path: {}",
                e.getResourcePath());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                "El recurso solicitado no existe"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDataIntegrityViolation(
            DataIntegrityViolationException e) {

        log.warn("REST data integrity violation. Reason: {}", e.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                "El usuario ya ha valorado esta película"
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGenericException(Exception e) {

        log.error("Unexpected REST error.", e);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Se ha producido un error interno en el servicio REST"
        );
    }

    private Map<String, Object> buildErrorResponse(
            HttpStatus status,
            String error,
            String message) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);

        return response;
    }
}