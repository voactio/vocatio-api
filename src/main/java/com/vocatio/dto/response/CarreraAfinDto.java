package com.vocatio.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarreraAfinDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String areaInteres;
    private Double porcentajeCompatibilidad;
}