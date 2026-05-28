package com.diego.filmotokio.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({
        "com.diego.filmotokio.batch.model",
        "com.diego.filmotokio.common.model"
})
public class FilmotokioBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmotokioBatchApplication.class, args);
    }
}