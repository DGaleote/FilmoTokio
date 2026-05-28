package com.diego.filmotokio.web.service;

import com.diego.filmotokio.common.model.AppUser;
import com.diego.filmotokio.web.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        AppUser appUser = appUserRepository.findByUsername(usernameOrEmail)
                .or(() -> appUserRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + usernameOrEmail));

        String[] roles = appUser.getRoles()
                .stream()
                .map(role -> role.getName())
                .toArray(String[]::new);

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(roles)
                .build();
    }
}