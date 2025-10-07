package com.vocatio.controller;

import com.vocatio.dto.request.LoginRequest;
import com.vocatio.dto.request.RegisterUsuarioRequest;
import com.vocatio.dto.request.UpdateUsuarioRequest;
import com.vocatio.model.Usuario;
import com.vocatio.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // FUNCIONALIDAD 1 - REGISTRAR NUEVO USUARIO
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUsuarioRequest request) {
        Usuario user = usuarioService.register(request);
        user.setContrasena(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // FUNCIONALIDAD 2 - MODIFICAR PERFIL
    @PatchMapping("/updPerfil/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUsuarioRequest request){

        try {
            Usuario usuarioModificado = usuarioService.updateUsuario(id, request);

            Map<String, Object> response = Map.of(
                    "mensaje", "Perfil modificado exitosamente",
                    "usuario", Map.of(
                            "id", usuarioModificado.getId(),
                            "nombre", usuarioModificado.getNombre(),
                            "correo", usuarioModificado.getCorreo(),
                            "nivelEducativo", usuarioModificado.getNivelEducativo(),
                            "carreraActual", usuarioModificado.getCarreraActual(),
                            "urlImagenPerfil", usuarioModificado.getUrlImagenPerfil(),
                            "creadoEn", usuarioModificado.getCreadoEn(),
                            "actualizadoEn", usuarioModificado.getActualizadoEn()
                    )
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // FUNCIONALIDAD 3 - INICIAR SESION (LOGIN)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(usuarioService.login(request));
    }
}

