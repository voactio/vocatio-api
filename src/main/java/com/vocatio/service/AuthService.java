package com.vocatio.service;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.response.AuthResponse;
import com.vocatio.model.Role;
import com.vocatio.model.RoleType;
import com.vocatio.model.Usuario;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.RoleRepository;
import com.vocatio.repository.UsuarioRepository;
import com.vocatio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CarreraRepository carreraRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public AuthResponse register(RegisterUsuarioRequest request) {
        // Validar correo repetido
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Validar contraseña
        String password = request.getContrasena();
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            throw new RuntimeException("Contraseña inválida: debe tener letras, números y al menos 8 caracteres");
        }

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(() -> new RuntimeException("El role USER no existe"));

        Usuario user = new Usuario();
        user.setNombre(request.getNombre());
        user.setCorreo(request.getCorreo());
        user.setContrasena(passwordEncoder.encode(request.getContrasena()));
        user.setNivelEducativo(request.getNivelEducativo());
        user.setUrlImagenPerfil(request.getUrlImagenPerfil());
        user.setRole(userRole);

        if(request.getCarreraId() != null) {
            user.setCarrera(carreraRepository.findById(request.getCarreraId()).orElseThrow());
        }

        Usuario savedUser = usuarioRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getCorreo(), savedUser.getNombre(), savedUser.getId());

        return new AuthResponse(token, savedUser.getCorreo(), savedUser.getNombre());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getContrasena()
                )
        );

        Usuario user = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(user.getCorreo(), user.getNombre(), user.getId());

        return new AuthResponse(token, user.getCorreo(), user.getNombre());
    }
}

