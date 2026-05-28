package com.diego.filmotokio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class FilmSummaryDTO {

    private final Long id;
    private final String title;
    private final Integer year;
    private final String poster;


}