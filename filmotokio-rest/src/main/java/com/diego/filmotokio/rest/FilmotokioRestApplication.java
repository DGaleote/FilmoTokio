package com.diego.filmotokio.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class FilmotokioRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmotokioRestApplication.class, args);
    }
}