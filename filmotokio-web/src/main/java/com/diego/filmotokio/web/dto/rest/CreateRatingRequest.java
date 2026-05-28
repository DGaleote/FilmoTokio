package com.diego.filmotokio.web.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateRatingRequest {

    private Long userId;

    private Long filmId;

    private Integer score;
}