package com.diego.filmotokio.web.service;

import com.diego.filmotokio.common.model.AppUser;
import com.diego.filmotokio.common.model.Role;
import com.diego.filmotokio.web.dto.RegisterUserDTO;
import com.diego.filmotokio.web.repository.AppUserRepository;
import com.diego.filmotokio.web.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private static final String DEFAULT_USER_ROLE = "USER";

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterUserDTO registerUserDTO) {

        validateRegisterUser(registerUserDTO);

        Role userRole = roleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseGet(this::createDefaultUserRole);

        AppUser appUser = new AppUser();
        appUser.setUsername(registerUserDTO.getUsername());
        appUser.setEmail(registerUserDTO.getEmail());
        appUser.setName(registerUserDTO.getName());
        appUser.setSurname(registerUserDTO.getSurname());
        appUser.setBirthDate(registerUserDTO.getBirthDate());
        appUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        appUser.setCreatedAt(LocalDateTime.now());
        appUser.setRoles(List.of(userRole));

        appUserRepository.save(appUser);
    }

    private void validateRegisterUser(RegisterUserDTO registerUserDTO) {

        if (!registerUserDTO.getPassword().equals(registerUserDTO.getPasswordBis())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        if (appUserRepository.existsByUsername(registerUserDTO.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        if (appUserRepository.existsByEmail(registerUserDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }
    }

    private Role createDefaultUserRole() {

        Role role = new Role();
        role.setName(DEFAULT_USER_ROLE);

        return roleRepository.save(role);
    }

    public void updateLastLoginAt(String username) {

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow();

        appUser.setLastLoginAt(LocalDateTime.now());

        appUserRepository.save(appUser);
    }

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
    }

}
