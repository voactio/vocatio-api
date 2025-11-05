package com.vocatio.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class GenerateReportResponse {
    private Long idResultado;
    private LocalDateTime completadoEn;
    // área -> puntaje (viene del jsonb)
    private Map<String, Integer> puntajes;
    // top 5 carreras calculadas
    private List<CarreraAfinDto> topCarreras;
}
