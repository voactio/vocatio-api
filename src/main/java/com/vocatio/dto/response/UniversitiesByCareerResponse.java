package com.vocatio.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UniversitiesByCareerResponse {
    private Long idCarrera;
    private String nombreCarrera;
    private List<ItemUniversidad> universidades;

    @Data
    @Builder
    public static class ItemUniversidad {
        private Long idUniversidad;
        private String nombreUniversidad;
        private String ubicacion;
        private Integer duracionAnios;        // viene de carreras
        private BigDecimal costoPorAnio;      // viene de universidad_carreras
        private String urlUniversidad;        // viene de universidades
        private String urlPlanEspecifico;     // viene de universidad_carreras
    }
}
