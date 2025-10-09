package com.vocatio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_anios")
    private Integer duracionAnios;

    @Column(length = 50)
    private String modalidad;

    public Carrera() {}

    public Carrera(String nombre, String descripcion, Integer duracionAnios, String modalidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionAnios = duracionAnios;
        this.modalidad = modalidad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getDuracionAnios() { return duracionAnios; }
    public void setDuracionAnios(Integer duracionAnios) { this.duracionAnios = duracionAnios; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
}

