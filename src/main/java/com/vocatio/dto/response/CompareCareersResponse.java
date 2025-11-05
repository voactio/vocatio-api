package com.vocatio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompareCareersResponse {

    private CareerCompareItem carrera1;
    private CareerCompareItem carrera2;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerCompareItem {
        private Long id;
        private String nombre;
        private String descripcion;
        private Integer DuracionAnios;
        private String modalidad;
        private String rangoSalarioPromedio;
        private String areaInteres;
    }
}
