package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "carrera_areas_interes")
@IdClass(CarreraAreaInteresId.class)
public class CarreraAreaInteres {
    @Id
    @Column(name = "id_carrera")
    private Long idCarrera;
    @Id
    @Column(name = "id_area_interes")
    private Long idAreaInteres;
    @Column(name = "puntaje_relevancia")
    private Integer puntajeRelevancia;
}
