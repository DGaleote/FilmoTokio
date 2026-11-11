package com.diego.filmotokio.web.service.rest;

import com.diego.filmotokio.web.dto.rest.CreateRatingRequest;
import com.diego.filmotokio.web.dto.rest.RatingAverageResponse;
import com.diego.filmotokio.web.dto.rest.RatingResponse;
import com.diego.filmotokio.web.exception.RatingAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RatingRestClient {

    private final RestAuthenticationService restAuthenticationService;

    @Value("${filmotokio.rest.base-url}")
    private String restBaseUrl;

    public RatingAverageResponse getAverageByFilm(Long filmId) {
        String token = restAuthenticationService.getAccessToken();

        String url = restBaseUrl + "/ratings-average/films/" + filmId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<RatingAverageResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                RatingAverageResponse.class
        );

        return response.getBody();
    }

    public RatingResponse getRatingByFilmAndUser(Long filmId, Long userId) {
        String token = restAuthenticationService.getAccessToken();

        String url = restBaseUrl + "/ratings/films/" + filmId + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<RatingResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    RatingResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }

    public void createRating(Long filmId, Long userId, Integer score) {
        String token = restAuthenticationService.getAccessToken();

        String url = restBaseUrl + "/ratings";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        CreateRatingRequest body = new CreateRatingRequest(
                userId,
                filmId,
                score
        );

        HttpEntity<CreateRatingRequest> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

        } catch (HttpClientErrorException.Conflict exception) {
            throw new RatingAlreadyExistsException();
        }
    }


}