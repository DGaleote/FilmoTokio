package com.diego.filmotokio.rest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    @Value("${filmotokio.rest.jwt.secret}")
    private String jwtSecret;

    @Getter
    @Value("${filmotokio.rest.jwt.expiration-seconds}")
    private long expirationSeconds;

    public String generateToken(String subject) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String validateTokenAndGetSubject(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getSubject();
    }
}