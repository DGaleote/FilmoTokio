package com.diego.filmotokio.web.mvc;

import com.diego.filmotokio.web.dto.RegisterUserDTO;
import com.diego.filmotokio.web.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegisterViewController {

    private final AppUserService appUserService;

    @GetMapping("/register")
    public String registerForm(Model model) {

        model.addAttribute("registerUserDTO", new RegisterUserDTO());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid RegisterUserDTO registerUserDTO,
            BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            appUserService.registerUser(registerUserDTO);
        } catch (IllegalArgumentException e) {
            result.reject("registerError", e.getMessage());
            return "register";
        }

        return "redirect:/";
    }
}