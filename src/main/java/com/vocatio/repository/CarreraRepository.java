package com.vocatio.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carreras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_anios", nullable = false)
    private Integer duracionAnios;

    @Column(name = "modalidad", length = 50)
    private String modalidad;

    @Column(name = "plan_estudios", columnDefinition = "TEXT")
    private String planEstudios;

    @Column(name = "rango_salario_promedio")
    private String rangoSalarioPromedio;

    @Column(name = "perfil_riasec")
    private String perfilRiasec;

    @ElementCollection
    @CollectionTable(name = "carrera_universidades", joinColumns = @JoinColumn(name = "carrera_id"))
    @Column(name = "universidad", length = 200)
    @Builder.Default
    private List<String> universidadesSugeridas = new ArrayList<>();
}