package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carrera_areas_interes")
@IdClass(CarreraAreaInteresId.class)
public class CarreraAreaInteres implements Serializable {

    @Id
    @Column(name = "id_carrera")
    private Long idCarrera;  // ← Ahora sí hay propiedad idCarrera

    @Id
    @Column(name = "id_area_interes")
    private Long idAreaInteres;

    @ManyToOne
    @JoinColumn(name = "id_carrera", insertable = false, updatable = false)
    private Carrera carrera;

    @ManyToOne
    @JoinColumn(name = "id_area_interes", insertable = false, updatable = false)
    private AreaInteres areaInteres;

    @Column(name = "puntaje_relevancia", nullable = false)
    private Integer puntajeRelevancia;
}
