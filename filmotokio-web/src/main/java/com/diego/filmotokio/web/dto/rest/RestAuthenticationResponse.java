package com.diego.filmotokio.web.dto.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestAuthenticationResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
}