package com.vocatio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carreras") // UNIFICADO: Usaremos 'carreras' como el nombre de tabla.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150) // Mantenemos el mapeo explícito
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT") // Mantenemos el mapeo explícito
    private String descripcion;

    // CAMBIOS AGREGADOS DE 'DEVELOP'
    @Column(name = "duracion_anios", nullable = false)
    private int duracionAnios;

    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "rango_salario_promedio")
    private String rangoSalarioPromedio;

    @Column(name = "perfil_riasec")
    private String perfilRiasec;

    // NOTA: Cuando implementaste el detalle de carrera, agregaste más campos a esta entidad.
    // Asumo que esos campos (planEstudios, universidadesSugeridas) estaban en otra rama.
    // Si esos campos no están aquí, recuerda que tendrás que agregarlos manualmente después
    // de este merge para que la funcionalidad de detalle de carrera esté completa.
}