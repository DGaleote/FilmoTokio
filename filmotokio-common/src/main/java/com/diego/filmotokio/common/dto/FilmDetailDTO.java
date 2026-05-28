package com.diego.filmotokio.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FilmDetailDTO {

    private final Long id;
    private final String title;
    private final Integer year;
    private final String synopsis;
    private final String director;
    private final List<String> actors;
    private final String poster;

}