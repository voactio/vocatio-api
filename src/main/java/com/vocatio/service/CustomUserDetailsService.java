package com.vocatio.service;

import com.vocatio.model.Usuario;
import com.vocatio.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByCorreo(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // Aquí usamos el rol del usuario almacenado en tu tabla Role
        String roleName = user.getRole() != null ? user.getRole().getName().name() : "USER";

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getCorreo())
                .password(user.getContrasena())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(roleName)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}
