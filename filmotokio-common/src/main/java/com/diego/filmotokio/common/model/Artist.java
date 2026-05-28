package com.diego.filmotokio.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "Los apellidos son obligatorios")
    private String surname;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    private ArtistType type;
}