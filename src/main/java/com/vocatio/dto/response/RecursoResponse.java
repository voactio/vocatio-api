package com.vocatio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecursoResponse {
    private String titulo;
    private String tipoRecurso;
    private String autor;
    private String url;
}