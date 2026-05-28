package com.diego.filmotokio.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.diego.filmotokio.common.model")
public class FilmotokioWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmotokioWebApplication.class, args);
    }

}