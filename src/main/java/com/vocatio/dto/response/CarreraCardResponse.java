package com.vocatio.dto.response;

public class CarreraCardResponse {
    private Long id;
    private String nombre;
    private String descripcionCorta;

    public CarreraCardResponse(Long id, String nombre, String descripcionCorta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcionCorta = descripcionCorta;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcionCorta() { return descripcionCorta; }
}
