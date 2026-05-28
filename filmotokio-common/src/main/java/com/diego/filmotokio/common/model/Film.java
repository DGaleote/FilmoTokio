package com.diego.filmotokio.common.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotNull(message = "El año es obligatorio")
    private Integer year;

    @Column(length = 2000)
    private String synopsis;

    @NotNull(message = "El director es obligatorio")
    @ManyToOne
    private Artist director;

    private String poster;

    @NotEmpty(message = "Debe seleccionar al menos un actor")
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Artist> actors;

    @ManyToOne
    private AppUser createdBy;
}