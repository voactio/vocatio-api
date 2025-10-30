package com.vocatio.dto.response;

import lombok.Data;

@Data
public class CarreraRankingDTO {
    private Long id;
    private String nombre;
    private String descripcionCorta;
    private String tagPrincipal;
    private int compatibilidad;
}