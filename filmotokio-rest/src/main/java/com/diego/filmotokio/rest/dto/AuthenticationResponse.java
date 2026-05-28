package com.diego.filmotokio.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
}