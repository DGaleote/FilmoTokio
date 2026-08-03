package com.diego.filmotokio.web.mvc;

import com.diego.filmotokio.common.dto.FilmDetailDTO;
import com.diego.filmotokio.common.model.AppUser;
import com.diego.filmotokio.common.model.ArtistType;
import com.diego.filmotokio.common.model.Film;
import com.diego.filmotokio.web.dto.rest.RatingResponse;
import com.diego.filmotokio.web.service.AppUserService;
import com.diego.filmotokio.web.service.ArtistService;
import com.diego.filmotokio.web.service.FilmService;
import com.diego.filmotokio.web.service.rest.RatingRestClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FilmViewController {

    private static final Logger log = LoggerFactory.getLogger(FilmViewController.class);
    private static final long MAX_POSTER_SIZE_BYTES = 2 * 1024 * 1024;

    private final FilmService filmService;
    private final ArtistService artistService;
    private final RatingRestClient ratingRestClient;
    private final AppUserService appUserService;

    @Value("${filmotokio.uploads.posters}")
    private String postersPath;

    @GetMapping({"/", "/films"})
    public String home(@RequestParam(value = "search", required = false) String search, Model model) {

        log.info("Loading film list. Search: {}", search);

        if (search != null && !search.isBlank()) {
            model.addAttribute("films", filmService.searchFilmSummariesByTitle(search));
        } else {
            model.addAttribute("films", filmService.getFilmSummaries());
        }

        return "film-list";
    }

    @GetMapping("/films/{id}")
    public String filmDetail(@PathVariable("id") Long id, Model model) {
        log.info("Loading film detail. Film id: {}", id);
        model.addAttribute("film", filmService.getFilmDetail(id));

        try {
            model.addAttribute("ratingAverage", ratingRestClient.getAverageByFilm(id));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            AppUser currentUser = appUserService.findByUsername(username);

            RatingResponse userRating = ratingRestClient.getRatingByFilmAndUser(
                    id,
                    currentUser.getId()
            );

            model.addAttribute("userRating", userRating);
            model.addAttribute("ratingsAvailable", true);

        } catch (RestClientException e) {

            log.warn("Ratings service unavailable while loading film detail. Film id: {}. Reason: {}",
                    id,
                    e.getMessage());

            model.addAttribute("ratingsAvailable", false);
            model.addAttribute("userRating", null);
        }

        return "film-detail";
    }

    @GetMapping("/films/create")
    public String createFilmForm(Model model) {

        model.addAttribute("film", new Film());
        loadFilmFormData(model);

        return "film-create";
    }

    @GetMapping("/films/{id}/edit")
    public String editFilmForm(@PathVariable("id") Long id, Model model) {

        model.addAttribute("film", filmService.findById(id));
        loadFilmFormData(model);

        return "film-create";
    }

//    @GetMapping("/films/{id}")
//    public String filmDetail(@PathVariable("id") Long id, Model model) {
//
//        FilmDetailDTO film = filmService.getFilmDetail(id);
//
//        model.addAttribute("film", film);
//
//        return "film-detail";
//    }

    @PostMapping("/films/create")
    public String createFilm(
            @Valid Film film,
            BindingResult result,
            @RequestParam("posterFile") MultipartFile posterFile,
            Model model) {

    /*
        El poster es obligatorio al crear una película nueva.
        En edición no lo exigimos porque la película puede conservar
        el poster que ya tenía asociado.
     */
        validatePosterRequiredOnCreate(film, posterFile, result);

    /*
        Si hay errores de validación, volvemos al formulario.
        Como no pasamos por los métodos GET, hay que recargar manualmente
        las listas necesarias para los desplegables de director y actores.
     */
        if (result.hasErrors()) {

            log.warn("Film form validation failed. Film id: {}, title: {}, errors: {}",
                    film.getId(),
                    film.getTitle(),
                    result.getErrorCount());

            loadFilmFormData(model);

            return "film-create";
        }

        try {
            if (!posterFile.isEmpty()) {
                film.setPoster(savePoster(posterFile));
            }
        } catch (IllegalArgumentException e) {

            log.warn("Invalid poster for film id: {}, title: {}. Reason: {}",
                    film.getId(),
                    film.getTitle(),
                    e.getMessage());

            result.rejectValue("poster", "poster.invalid", e.getMessage());

            restoreExistingPosterIfEditing(film);
            loadFilmFormData(model);

            return "film-create";

        } catch (IOException e) {

            log.error("Could not save poster for film id: {}, title: {}",
                    film.getId(),
                    film.getTitle(),
                    e);

            result.rejectValue("poster", "poster.saveError", "No se pudo guardar el poster");

            restoreExistingPosterIfEditing(film);
            loadFilmFormData(model);

            return "film-create";
        }

    /*
        Si estamos editando una película y no se ha subido un poster nuevo,
        recuperamos el poster existente para no sobrescribirlo con null.
     */
        preserveExistingPosterIfNeeded(film, posterFile);

        log.info("Processing film form. Film id: {}, title: {}", film.getId(), film.getTitle());

        filmService.save(film);

        log.info("Film saved successfully. Film id: {}, title: {}", film.getId(), film.getTitle());

        return "redirect:/films";
    }

    private void preserveExistingPosterIfNeeded(Film film, MultipartFile posterFile) {

        if (posterFile.isEmpty()) {
            restoreExistingPosterIfEditing(film);
        }
    }

    private void restoreExistingPosterIfEditing(Film film) {

        if (film.getId() != null) {
            Film existingFilm = filmService.findById(film.getId());
            film.setPoster(existingFilm.getPoster());
        }
    }

    @PostMapping("/films/{id}/delete")
    public String deleteFilm(@PathVariable("id") Long id) {

        log.info("Deleting film. Film id: {}", id);

        try {
            filmService.deleteById(id);

            log.info("Film deleted successfully. Film id: {}", id);

            return "redirect:/films";

        } catch (RuntimeException e) {

            log.error("Could not delete film. Film id: {}", id, e);

            throw e;
        }
    }

    private void loadFilmFormData(Model model) {
        model.addAttribute("directors",
                artistService.findByType(ArtistType.DIRECTOR));

        model.addAttribute("actors",
                artistService.findByType(ArtistType.ACTOR));
    }


    private String savePoster(MultipartFile posterFile) throws IOException {


        if (posterFile.getSize() > MAX_POSTER_SIZE_BYTES) {
            throw new IllegalArgumentException("El poster no puede superar los 2 MB");
        }

        String contentType = posterFile.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/webp"))) {

            throw new IllegalArgumentException("El poster debe ser una imagen JPG, PNG o WEBP");
        }

        String originalName = posterFile.getOriginalFilename();

        if (originalName == null) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        int extensionIndex = originalName.lastIndexOf(".");

        if (extensionIndex <= 0 || extensionIndex == originalName.length() - 1) {
            throw new IllegalArgumentException("El archivo debe tener una extensión válida");
        }

        String extension = originalName.substring(extensionIndex).toLowerCase();

        if (!(extension.equals(".jpg")
                || extension.equals(".jpeg")
                || extension.equals(".png")
                || extension.equals(".webp"))) {

            throw new IllegalArgumentException("El poster debe tener extensión JPG, PNG o WEBP");
        }

        String fileName = UUID.randomUUID() + extension;

        Path uploadPath = Paths.get(postersPath);
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(
                posterFile.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return fileName;
    }


    private void validatePosterRequiredOnCreate(
            Film film,
            MultipartFile posterFile,
            BindingResult result) {

        if (film.getId() == null && posterFile.isEmpty()) {
            result.rejectValue("poster", "poster.required", "El poster es obligatorio");
        }
    }

    @PostMapping("/films/{id}/ratings")
    public String rateFilm(
            @PathVariable("id") Long id,
            @RequestParam("score") Integer score) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        AppUser currentUser = appUserService.findByUsername(username);

        log.info("Creating rating. Film id: {}, user id: {}, score: {}",
                id,
                currentUser.getId(),
                score);

        ratingRestClient.createRating(
                id,
                currentUser.getId(),
                score
        );

        log.info("Rating created successfully. Film id: {}, user id: {}, score: {}",
                id,
                currentUser.getId(),
                score);

        return "redirect:/films/" + id;
    }

}