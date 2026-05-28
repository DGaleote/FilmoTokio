package com.diego.filmotokio.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RatingAverageResponse {

    private BigDecimal average;

    private long ratings;
}