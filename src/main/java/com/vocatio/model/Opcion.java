package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "opciones")
@Data
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String textoOpcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta")
    private Pregunta pregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area_interes")
    private AreasInteres areaInteres;
}