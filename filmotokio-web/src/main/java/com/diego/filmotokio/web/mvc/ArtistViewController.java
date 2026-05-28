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

@Controller
@RequiredArgsConstructor
public class ArtistViewController {

    private final ArtistService artistService;

    @GetMapping("/artists")
    public String listArtists(Model model) {
        model.addAttribute("artists", artistService.findAll());
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
            return "artist-create";
        }

        artistService.createArtist(artist);
        return "redirect:/artists";
    }

    @GetMapping("/artists/{id}/edit")
    public String editArtistForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", artistService.findById(id));
        return "artist-create";
    }

    @PostMapping("/artists/{id}/delete")
    public String deleteArtist(@PathVariable("id") Long id) {
        artistService.deleteById(id);
        return "redirect:/artists";
    }
}