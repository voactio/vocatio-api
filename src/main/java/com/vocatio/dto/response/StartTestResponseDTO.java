package com.vocatio.dto.response;

import lombok.Data;

@Data
public class StartTestResponseDTO {
    private Long sessionId;
    private PreguntaDTO primeraPregunta;
}