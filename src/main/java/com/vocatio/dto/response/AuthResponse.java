package com.vocatio.dto.response;

import java.util.UUID;

public record AuthResponse(
        String token,
        String type,
        String correo,
        String nombre,
        UUID id,
        String nivelEducativo,
        Long carreraId,
        String urlImagenPerfil
) {
    public AuthResponse(String token, String correo, String nombre, UUID id,
                        String nivelEducativo, Long carreraId, String urlImagenPerfil) {
        this(token, "Bearer", correo, nombre, id, nivelEducativo, carreraId, urlImagenPerfil);
    }
}
