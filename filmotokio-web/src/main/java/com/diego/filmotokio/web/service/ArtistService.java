package com.diego.filmotokio.web.service;

import com.diego.filmotokio.common.model.Artist;
import com.diego.filmotokio.common.model.ArtistType;
import com.diego.filmotokio.web.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public void createArtist(Artist artist) {
        artistRepository.save(artist);
    }

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }

    public List<Artist> findByType(ArtistType type) {
        return artistRepository.findByType(type);
    }
}