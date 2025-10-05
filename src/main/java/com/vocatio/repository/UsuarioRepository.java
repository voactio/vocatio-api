package com.vocatio.repository;

import com.vocatio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// La tabla users está asociada a la entidad "Usuario"
// Long es el tipo de clave primaria (id) de "Usuario"
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // puede contener un usuario o estar vacio
    // query: SELECT * FROM users WHERE correo = ?;
    Optional<Usuario> findByCorreo(String correo);
    // existe un usuario con ese correo?
    boolean existsByCorreo(String correo);
}