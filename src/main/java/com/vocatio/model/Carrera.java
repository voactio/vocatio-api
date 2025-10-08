package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carreras")
@Data
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private int duracionAnios;

    private String modalidad;

    private String rangoSalarioPromedio;

    @Column(name = "perfil_riasec")
    private String perfilRiasec;
}