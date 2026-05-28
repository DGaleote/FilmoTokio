package com.diego.filmotokio.batch.writer;

import com.diego.filmotokio.batch.service.FilmExportService;
import com.diego.filmotokio.common.model.Film;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmCsvWriter implements ItemWriter<Film>, StepExecutionListener {

    private final FilmExportService filmExportService;

    @Value("${filmotokio.batch.export.directory}")
    private String exportDirectory;

    private Long jobId;

    public FilmCsvWriter(FilmExportService filmExportService) {
        this.filmExportService = filmExportService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.jobId = stepExecution.getJobExecutionId();
    }

    @Override
    public void write(Chunk<? extends Film> chunk) {
        Path exportDirectoryPath = Paths.get(exportDirectory);
        Path path = exportDirectoryPath.resolve("films-export-job-" + jobId + ".csv");

        List<Film> exportedFilms = new ArrayList<>();

        try {
            Files.createDirectories(exportDirectoryPath);

            boolean fileAlreadyExists = Files.exists(path);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                if (!fileAlreadyExists) {
                    writer.write("filmId,title,releaseYear");
                    writer.newLine();
                }

                for (Film film : chunk) {
                    writer.write(
                            film.getId() + "," +
                                    escapeCsv(film.getTitle()) + "," +
                                    film.getYear()
                    );
                    writer.newLine();

                    exportedFilms.add(film);
                }
            }

            filmExportService.markFilmsAsExported(exportedFilms, jobId);

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo CSV de películas", e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue = value.strip().replace("\"", "\"\"");

        if (escapedValue.contains(",")
                || escapedValue.contains("\"")
                || escapedValue.contains("\n")
                || escapedValue.contains("\r")) {

            return "\"" + escapedValue + "\"";
        }

        return escapedValue;
    }

}