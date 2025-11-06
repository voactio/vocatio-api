package com.vocatio.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vocatio.controller.UsuarioController;
import com.vocatio.dto.request.UpdateUsuarioRequest;
import com.vocatio.model.Carrera;
import com.vocatio.model.Usuario;
import com.vocatio.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(UsuarioController.class)
@DisplayName("UsuarioController - Pruebas funcionales (sin @MockBean, carrera null)")
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioService usuarioService;

    private UsuarioController usuarioController;

    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inyectar el mock
        usuarioController = new UsuarioController(usuarioService);

        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();

        usuarioId = UUID.randomUUID();

        // Usuario de prueba
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@vocatio.com");
        usuario.setNivelEducativo("Universitario");

        // Carrera con valor asignado
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingeniería de Software");
        usuario.setCarrera(carrera);

        usuario.setUrlImagenPerfil("http://imagen.com/perfil.jpg");
        usuario.setCreadoEn(LocalDateTime.now().minusDays(1));
        usuario.setActualizadoEn(LocalDateTime.now());
    }

    // UpdatePerfil - CONT 1 - Modificar perfil exitoso
    @Test
    @DisplayName("PATCH /usuarios/updPerfil/{id} - modificación exitosa")
    void updateUsuarioExitoso() throws Exception {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setNombre("Usuario Modificado");
        request.setContrasena("Password123"); // válida
        request.setNivelEducativo("Universitario");
        request.setCarreraId(usuario.getCarrera().getId()); // Carrera existente
        request.setUrlImagenPerfil("http://imagen.com/nueva.jpg");

        // Usuario devuelto por el servicio
        Usuario usuarioModificado = new Usuario();
        usuarioModificado.setId(usuarioId);
        usuarioModificado.setNombre(request.getNombre());
        usuarioModificado.setCorreo(usuario.getCorreo());
        usuarioModificado.setNivelEducativo(request.getNivelEducativo());
        usuarioModificado.setCarrera(usuario.getCarrera()); // Carrera con valor
        usuarioModificado.setUrlImagenPerfil(request.getUrlImagenPerfil());
        usuarioModificado.setCreadoEn(usuario.getCreadoEn());
        usuarioModificado.setActualizadoEn(LocalDateTime.now());

        when(usuarioService.updateUsuario(eq(usuarioId), any(UpdateUsuarioRequest.class)))
                .thenReturn(usuarioModificado);

        mockMvc.perform(patch("/usuarios/updPerfil/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario actualizado correctamente"))
                .andExpect(jsonPath("$.usuario.nombre").value("Usuario Modificado"))
                .andExpect(jsonPath("$.usuario.urlImagenPerfil").value("http://imagen.com/nueva.jpg"))
                .andExpect(jsonPath("$.usuario.carreraId").value(usuario.getCarrera().getId().toString()));
    }

    // UpdatePerfil - CONT 2 - Modificar perfil erroneo - Contraseña invalida
    @Test
    @DisplayName("PATCH /usuarios/updPerfil/{id} - contraseña inválida")
    void updateUsuarioContrasenaInvalida() throws Exception {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setNombre("Usuario Modificado");
        request.setContrasena("abc123"); // inválida
        request.setNivelEducativo("Universitario");
        request.setCarreraId(usuario.getCarrera().getId()); // Carrera existente

        when(usuarioService.updateUsuario(eq(usuarioId), any(UpdateUsuarioRequest.class)))
                .thenThrow(new RuntimeException("Contraseña inválida: debe tener letras, números y al menos 8 caracteres"));

        mockMvc.perform(patch("/usuarios/updPerfil/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
