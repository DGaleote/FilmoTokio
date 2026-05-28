package com.diego.filmotokio.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRatingRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    @NotNull(message = "La película es obligatoria")
    private Long filmId;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer score;
}