package com.vocatio.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CrearTestimonioRequest {
    private UUID idUsuario;
    private String textoTestimonio;
}