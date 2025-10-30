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

import java.util.HashMap;
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

            if (usuarioModificado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
            }

            Map<String, Object> usuarioM = new HashMap<>();
            usuarioM.put("id", usuarioModificado.getId());
            usuarioM.put("nombre", usuarioModificado.getNombre());
            usuarioM.put("correo", usuarioModificado.getCorreo());
            usuarioM.put("nivelEducativo", usuarioModificado.getNivelEducativo());
            usuarioM.put("carreraActual", usuarioModificado.getCarreraActual());
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

    // FUNCIONALIDAD 3 - INICIAR SESION (LOGIN)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        try{
            Map<String, Object> response = usuarioService.login(request);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", e.getMessage()));
        }
    }
}

