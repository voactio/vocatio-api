package com.vocatio.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PreguntaDTO {
    private Long id;
    private String textoPregunta;
    private String progreso; // Ej: "Pregunta 2 de 30"
    private List<OpcionDTO> opciones;
}