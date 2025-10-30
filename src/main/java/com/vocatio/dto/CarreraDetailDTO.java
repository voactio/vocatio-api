package com.vocatio.dto;

import java.util.List;

public class CarreraDetailDTO {
    private Long id;
    private String nombre;
    private Integer duracionAnios;
    private String modalidad;
    private String descripcion;
    private String planEstudios;
    private List<String> universidadesSugeridas;

    public CarreraDetailDTO() {}

    public CarreraDetailDTO(Long id, String nombre, Integer duracionAnios, String modalidad, String descripcion, String planEstudios, List<String> universidadesSugeridas) {
        this.id = id;
        this.nombre = nombre;
        this.duracionAnios = duracionAnios;
        this.modalidad = modalidad;
        this.descripcion = descripcion;
        this.planEstudios = planEstudios;
        this.universidadesSugeridas = universidadesSugeridas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getDuracionAnios() { return duracionAnios; }
    public void setDuracionAnios(Integer duracionAnios) { this.duracionAnios = duracionAnios; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getPlanEstudios() { return planEstudios; }
    public void setPlanEstudios(String planEstudios) { this.planEstudios = planEstudios; }
    public List<String> getUniversidadesSugeridas() { return universidadesSugeridas; }
    public void setUniversidadesSugeridas(List<String> universidadesSugeridas) { this.universidadesSugeridas = universidadesSugeridas; }
}

