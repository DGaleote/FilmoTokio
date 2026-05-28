package com.diego.filmotokio.web.mvc;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FilmViewController {

    private static final long MAX_POSTER_SIZE_BYTES = 2 * 1024 * 1024;

    private final FilmService filmService;
    private final ArtistService artistService;
    private final RatingRestClient ratingRestClient;
    private final AppUserService appUserService;

    @Value("${filmotokio.uploads.posters}")
    private String postersPath;

    @GetMapping("/")
    public String home(@RequestParam(value = "search", required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("films", filmService.searchFilmSummariesByTitle(search));
        } else {
            model.addAttribute("films", filmService.getFilmSummaries());
        }
        return "film-list";
    }

    @GetMapping("/films/{id}/view")
    public String filmDetail(@PathVariable("id") Long id, Model model) {
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

            loadFilmFormData(model);

            return "film-create";
        }

        try {
            if (!posterFile.isEmpty()) {
                film.setPoster(savePoster(posterFile));
            }
        } catch (IllegalArgumentException e) {

            result.rejectValue("poster", "poster.invalid", e.getMessage());

            restoreExistingPosterIfEditing(film);
            loadFilmFormData(model);

            return "film-create";

        } catch (IOException e) {

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

        filmService.save(film);

        return "redirect:/";
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

        filmService.deleteById(id);

        return "redirect:/";
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

        ratingRestClient.createRating(
                id,
                currentUser.getId(),
                score
        );

        return "redirect:/films/" + id + "/view";
    }

}