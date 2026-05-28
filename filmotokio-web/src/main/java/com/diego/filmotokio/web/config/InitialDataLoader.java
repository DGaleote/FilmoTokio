package com.diego.filmotokio.web.config;

import com.diego.filmotokio.common.model.AppUser;
import com.diego.filmotokio.common.model.Role;
import com.diego.filmotokio.web.repository.AppUserRepository;
import com.diego.filmotokio.web.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseGet(() -> createRole(ROLE_USER));

        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseGet(() -> createRole(ROLE_ADMIN));

        if (!appUserRepository.existsByUsername("admin")) {
            AppUser admin = new AppUser();

            admin.setUsername("admin");
            admin.setEmail("admin@filmotokio.local");
            admin.setName("Administrador");
            admin.setSurname("FilmoTokio");
            admin.setPassword(passwordEncoder.encode("change_me_admin"));
            admin.setBirthDate(LocalDate.of(1970, 1, 1));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setRoles(List.of(userRole, adminRole));

            appUserRepository.save(admin);
        }
    }

    private Role createRole(String roleName) {

        Role role = new Role();
        role.setName(roleName);

        return roleRepository.save(role);
    }
}