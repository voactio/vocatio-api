package com.vocatio.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TestHistoryResponse {
    private Long idResultado;
    private LocalDateTime fecha;
    private Integer intento;
    private List<CarreraAfinDto> topCarreras;
}