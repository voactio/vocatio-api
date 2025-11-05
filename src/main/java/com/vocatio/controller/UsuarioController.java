package com.vocatio.controller;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.request.UpdateUsuarioRequest;
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

            Map<String, Object> usuarioM = new HashMap<>();
            usuarioM.put("id", usuarioModificado.getId());
            usuarioM.put("nombre", usuarioModificado.getNombre());
            usuarioM.put("correo", usuarioModificado.getCorreo());
            usuarioM.put("nivelEducativo", usuarioModificado.getNivelEducativo());
            usuarioM.put("carreraId", usuarioModificado.getCarrera().getId());
            usuarioM.put("urlImagenPerfil",  usuarioModificado.getUrlImagenPerfil());
            usuarioM.put("creadoEn", usuarioModificado.getCreadoEn());
            usuarioM.put("actualizadoEn", usuarioModificado.getActualizadoEn());

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario actualizado correctamente");
            response.put("usuario", usuarioM);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

}

