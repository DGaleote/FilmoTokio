package com.diego.filmotokio.web.mvc;

import com.diego.filmotokio.web.dto.RegisterUserDTO;
import com.diego.filmotokio.web.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class RegisterViewController {
    private static final Logger log = LoggerFactory.getLogger(RegisterViewController.class);
    private final AppUserService appUserService;

    @GetMapping("/register")
    public String registerForm(Model model, Authentication authentication) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/films";
        }

        model.addAttribute("registerUserDTO", new RegisterUserDTO());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid RegisterUserDTO registerUserDTO,
            BindingResult result,
            Authentication authentication) {

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            log.info("Authenticated user tried to access register POST. Redirecting to films.");

            return "redirect:/films";
        }

        if (result.hasErrors()) {

            log.warn("Register form validation failed. Username: {}, email: {}, errors: {}",
                    registerUserDTO.getUsername(),
                    registerUserDTO.getEmail(),
                    result.getErrorCount());

            return "register";
        }

        try {
            log.info("Registering new user. Username: {}, email: {}",
                    registerUserDTO.getUsername(),
                    registerUserDTO.getEmail());

            appUserService.registerUser(registerUserDTO);

            log.info("User registered successfully. Username: {}, email: {}",
                    registerUserDTO.getUsername(),
                    registerUserDTO.getEmail());

        } catch (IllegalArgumentException e) {

            log.warn("User registration failed. Username: {}, email: {}. Reason: {}",
                    registerUserDTO.getUsername(),
                    registerUserDTO.getEmail(),
                    e.getMessage());

            result.reject("registerError", e.getMessage());
            return "register";
        }

        return "redirect:/";
    }
}