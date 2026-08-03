package com.diego.filmotokio.web.mvc;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginViewController {
    private static final Logger log = LoggerFactory.getLogger(LoginViewController.class);

    @GetMapping("/login")
    public String loginForm(Authentication authentication) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            log.info("Authenticated user tried to access login page. Redirecting to films. User: {}",
                    authentication.getName());

            return "redirect:/films";
        }

        log.info("Showing login page.");

        return "login";
    }
}