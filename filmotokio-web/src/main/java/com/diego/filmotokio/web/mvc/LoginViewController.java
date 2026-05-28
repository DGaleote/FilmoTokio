package com.diego.filmotokio.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}