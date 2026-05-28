package com.diego.filmotokio.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RatingResponse {

    private Integer score;

    private LocalDateTime createdAt;
}