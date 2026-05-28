package com.diego.filmotokio.web.dto.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RatingResponse {

    private Integer score;

    private LocalDateTime createdAt;
}