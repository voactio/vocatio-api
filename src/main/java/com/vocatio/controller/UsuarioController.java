package com.vocatio.controller;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.request.UpdateUsuarioRequest;
import com.vocatio.dto.response.UsuarioResponse;
import com.vocatio.model.Usuario;
import com.vocatio.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // FUNCIONALIDAD 2 - MODIFICAR PERFIL
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/updPerfil/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUsuarioRequest request){

        try {
            Usuario usuarioModificado = usuarioService.updateUsuario(id, request);

            if (usuarioModificado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
            }

            UsuarioResponse response = new UsuarioResponse(
                    usuarioModificado.getId(),
                    usuarioModificado.getNombre(),
                    usuarioModificado.getCorreo(),
                    usuarioModificado.getNivelEducativo(),
                    usuarioModificado.getCarrera() != null
                            ? usuarioModificado.getCarrera().getId()
                            : null,
                    usuarioModificado.getUrlImagenPerfil()
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }


    // FUNCIONALIDAD ADICIONAL - OBTENER USUARIO POR ID
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable UUID id) {

        try {
            Usuario user = usuarioService.getUsuarioById(id);

            UsuarioResponse response = new UsuarioResponse(
                    user.getId(),
                    user.getNombre(),
                    user.getCorreo(),
                    user.getNivelEducativo(),
                    user.getCarrera() != null ? user.getCarrera().getId() : null,
                    user.getUrlImagenPerfil()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}

