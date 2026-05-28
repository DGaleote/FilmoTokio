package com.diego.filmotokio.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "Debe repetir la contraseña")
    private String passwordBis;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String surname;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
}