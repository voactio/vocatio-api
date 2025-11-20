package com.vocatio.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nombre,
        String correo,
        String nivelEducativo,
        Long carreraId,
        String urlImagenPerfil
) {}

