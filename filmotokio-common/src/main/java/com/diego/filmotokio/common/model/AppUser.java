package com.diego.filmotokio.common.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private String username;

    private String email;

    private String name;

    private String surname;

    private String password;

    private LocalDate birthDate;

    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}