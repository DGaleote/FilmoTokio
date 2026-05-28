package com.diego.filmotokio.rest.controller;

import com.diego.filmotokio.rest.dto.AuthenticationResponse;
import com.diego.filmotokio.rest.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;

    @Value("${filmotokio.rest.client.username}")
    private String clientUsername;

    @Value("${filmotokio.rest.client.password}")
    private String clientPassword;

    @PostMapping(
            value = "/authenticate",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestParam(value = "grant_type", required = false) String grantType
    ) {
        if (!"client_credentials".equals(grantType)) {
            return ResponseEntity.badRequest().build();
        }

        if (!isValidBasicAuthentication(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(clientUsername);

        AuthenticationResponse response = new AuthenticationResponse(
                token,
                "bearer",
                jwtService.getExpirationSeconds()
        );

        return ResponseEntity.ok(response);
    }

    private boolean isValidBasicAuthentication(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return false;
        }

        String encodedCredentials = authorizationHeader.substring("Basic ".length());

        try {
            String decodedCredentials = new String(
                    Base64.getDecoder().decode(encodedCredentials),
                    StandardCharsets.UTF_8
            );

            String expectedCredentials = clientUsername + ":" + clientPassword;

            return expectedCredentials.equals(decodedCredentials);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}