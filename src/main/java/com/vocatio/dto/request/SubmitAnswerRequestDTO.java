package com.vocatio.dto.request;

import lombok.Data;

@Data
public class SubmitAnswerRequestDTO {
    private Long preguntaId;
    private Long opcionId;
}