package com.diego.filmotokio.web.service.rest;

import com.diego.filmotokio.web.dto.rest.RestAuthenticationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RestAuthenticationService {

    @Value("${filmotokio.rest.base-url}")
    private String restBaseUrl;

    @Value("${filmotokio.rest.client.username}")
    private String clientUsername;

    @Value("${filmotokio.rest.client.password}")
    private String clientPassword;

    public String getAccessToken() {
        String url = restBaseUrl + "/authenticate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, buildBasicAuthorizationHeader());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<RestAuthenticationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                RestAuthenticationResponse.class
        );

        RestAuthenticationResponse authenticationResponse = response.getBody();

        if (authenticationResponse == null || authenticationResponse.getAccessToken() == null) {
            throw new IllegalStateException("No se pudo obtener token del servicio REST");
        }

        return authenticationResponse.getAccessToken();
    }

    private String buildBasicAuthorizationHeader() {
        String credentials = clientUsername + ":" + clientPassword;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encodedCredentials;
    }
}