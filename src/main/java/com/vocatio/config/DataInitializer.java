package com.vocatio.config;

import com.vocatio.model.Role;
import com.vocatio.model.RoleType;
import com.vocatio.model.Usuario;
import com.vocatio.repository.RoleRepository;
import com.vocatio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Inicializando base de datos con roles y usuario admin...");

        // Crear roles si no existen
        Role roleUser = createRoleIfNotExists(RoleType.ROLE_USER);
        Role roleAdmin = createRoleIfNotExists(RoleType.ROLE_ADMIN);

        // Crear usuario admin
        createAdminIfNotExists(roleAdmin);

        log.info("Inicialización completada.");
    }

    private Role createRoleIfNotExists(RoleType roleType) {
        return roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleType);
                    roleRepository.save(newRole);
                    log.info("Rol creado: {}", roleType);
                    return newRole;
                });
    }

    private void createAdminIfNotExists(Role adminRole) {
        String correoAdmin = "admin@vocatio.com";

        if (usuarioRepository.existsByCorreo(correoAdmin)) {
            log.info("El usuario admin ya existe: {}", correoAdmin);
            return;
        }

        Usuario admin = new Usuario();
        admin.setCorreo(correoAdmin);
        admin.setNombre("Administrador del Sistema");
        admin.setContrasena(passwordEncoder.encode("admin12345"));
        admin.setRole(adminRole);
        admin.setNivelEducativo("N/A");
        admin.setUrlImagenPerfil(null);

        usuarioRepository.save(admin);

        log.info("==============================================");
        log.info("USUARIO ADMIN CREADO POR DEFECTO");
        log.info("Correo: {}", correoAdmin);
        log.info("Contraseña: admin123");
        log.info("==============================================");
    }
}
