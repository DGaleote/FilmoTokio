package com.diego.filmotokio.web.mvc;

import com.diego.filmotokio.common.model.Artist;
import com.diego.filmotokio.web.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.diego.filmotokio.common.model.ArtistType;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArtistViewController {
    private static final Logger log = LoggerFactory.getLogger(ArtistViewController.class);
    private final ArtistService artistService;

    @GetMapping("/artists")
    public String listArtists(Model model) {

        List<Artist> artists = artistService.findAll();

        artists.sort(
                Comparator.comparing(
                                (Artist artist) ->
                                        artist.getType() == ArtistType.DIRECTOR ? 0 : 1
                        )
                        .thenComparing(Artist::getSurname)
                        .thenComparing(Artist::getName)
        );

        model.addAttribute("artists", artists);

        return "artist-list";
    }

    @GetMapping("/artists/create")
    public String createArtistForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "artist-create";
    }

    @PostMapping("/artists/create")
    public String createArtist(@Valid Artist artist, BindingResult result) {

        if (result.hasErrors()) {

            log.warn("Artist form validation failed. Name: {}, surname: {}, errors: {}",
                    artist.getName(),
                    artist.getSurname(),
                    result.getErrorCount());

            return "artist-create";
        }

        log.info("Creating artist. Name: {}, surname: {}, type: {}",
                artist.getName(),
                artist.getSurname(),
                artist.getType());

        artistService.createArtist(artist);

        log.info("Artist saved successfully. Name: {}, surname: {}, type: {}",
                artist.getName(),
                artist.getSurname(),
                artist.getType());

        return "redirect:/artists";
    }

    @GetMapping("/artists/{id}/edit")
    public String editArtistForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", artistService.findById(id));
        return "artist-create";
    }

    @PostMapping("/artists/{id}/delete")
    public String deleteArtist(@PathVariable("id") Long id) {

        log.info("Deleting artist. Artist id: {}", id);

        try {
            artistService.deleteById(id);

            log.info("Artist deleted successfully. Artist id: {}", id);

            return "redirect:/artists";

        } catch (RuntimeException e) {

            log.error("Artist deletion failed. Artist id: {}", id, e);

            throw e;
        }
    }
}