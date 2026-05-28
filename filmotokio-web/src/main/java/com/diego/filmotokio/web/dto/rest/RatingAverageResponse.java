package com.diego.filmotokio.web.dto.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RatingAverageResponse {

    private BigDecimal average;
    private int ratings;
}