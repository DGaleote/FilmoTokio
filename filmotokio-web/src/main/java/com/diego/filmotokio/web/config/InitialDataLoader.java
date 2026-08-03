package com.diego.filmotokio.web.config;

import com.diego.filmotokio.common.model.AppUser;
import com.diego.filmotokio.common.model.Role;
import com.diego.filmotokio.web.repository.AppUserRepository;
import com.diego.filmotokio.web.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${filmotokio.admin.username}")
    private String adminUsername;

    @Value("${filmotokio.admin.password}")
    private String adminPassword;

    @Value("${filmotokio.admin.email}")
    private String adminEmail;

    @Value("${filmotokio.admin.name}")
    private String adminName;

    @Value("${filmotokio.admin.surname}")
    private String adminSurname;

    @Value("${filmotokio.admin.birth-date}")
    private LocalDate adminBirthDate;

    @Override
    public void run(String... args) {
        Role userRole = getOrCreateRole(ROLE_USER);
        Role adminRole = getOrCreateRole(ROLE_ADMIN);

        createAdminIfMissing(userRole, adminRole);
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> createRole(roleName));
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);

        return roleRepository.save(role);
    }

    private void createAdminIfMissing(Role userRole, Role adminRole) {
        if (appUserRepository.existsByUsername(adminUsername)) {
            return;
        }

        AppUser admin = new AppUser();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEmail(adminEmail);
        admin.setName(adminName);
        admin.setSurname(adminSurname);
        admin.setBirthDate(adminBirthDate);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setRoles(List.of(userRole, adminRole));

        appUserRepository.save(admin);
    }
}