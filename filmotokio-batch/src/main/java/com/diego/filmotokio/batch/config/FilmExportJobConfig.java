package com.diego.filmotokio.batch.config;

import com.diego.filmotokio.batch.reader.FilmPendingExportReader;
import com.diego.filmotokio.batch.writer.FilmCsvWriter;
import com.diego.filmotokio.common.model.Film;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FilmExportJobConfig {

    @Value("${filmotokio.batch.chunk-size}")
    private int chunkSize;

    @Bean
    public Job filmExportJob(
            JobRepository jobRepository,
            Step filmExportStep) {

        return new JobBuilder("filmExportJob", jobRepository)
                .start(filmExportStep)
                .build();
    }
    @Bean
    public Step filmExportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FilmPendingExportReader reader,
            FilmCsvWriter writer) {

        return new StepBuilder("filmExportStep", jobRepository)
                .<Film, Film>chunk(chunkSize, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}