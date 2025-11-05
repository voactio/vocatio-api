package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "universidad_carreras")
@Getter
@Setter
public class UniversidadCarrera {

    @EmbeddedId
    private UniversidadCarreraId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUniversidad")
    @JoinColumn(name = "id_universidad")
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCarrera")
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @Column(name = "costo_por_anio")
    private BigDecimal costoPorAnio;

    @Column(name = "url_plan_especifico")
    private String urlPlanEspecifico;
}
