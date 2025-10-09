package com.vocatio.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "duracion_anios")
    private Integer duracionAnios;

    @Column(length = 50)
    private String modalidad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "plan_estudios", columnDefinition = "TEXT")
    private String planEstudios;

    @ElementCollection
    @CollectionTable(name = "carrera_universidades", joinColumns = @JoinColumn(name = "carrera_id"))
    @Column(name = "universidad", length = 200)
    private List<String> universidadesSugeridas = new ArrayList<>();

    public Carrera() {
    }

    public Carrera(String nombre, String descripcion, Integer duracionAnios, String modalidad, String planEstudios, List<String> universidadesSugeridas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionAnios = duracionAnios;
        this.modalidad = modalidad;
        this.planEstudios = planEstudios;
        if (universidadesSugeridas != null) this.universidadesSugeridas = universidadesSugeridas;
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

