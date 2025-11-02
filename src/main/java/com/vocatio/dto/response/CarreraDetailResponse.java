
package com.vocatio.dto.response;

import java.time.LocalDateTime;

public class CarreraDetailResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionAnios;
    private String modalidad;
    private String rangoSalarioPromedio;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public CarreraDetailResponse(Long id, String nombre, String descripcion,
                                 Integer duracionAnios, String modalidad,
                                 String rangoSalarioPromedio,
                                 LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionAnios = duracionAnios;
        this.modalidad = modalidad;
        this.rangoSalarioPromedio = rangoSalarioPromedio;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getDuracionAnios() { return duracionAnios; }
    public String getModalidad() { return modalidad; }
    public String getRangoSalarioPromedio() { return rangoSalarioPromedio; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}
