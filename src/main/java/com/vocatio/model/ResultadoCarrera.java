package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resultado_carreras")
@Data
@NoArgsConstructor
public class ResultadoCarrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resultado")
    private ResultadosTest resultadoTest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    private Double porcentaje;
    private Integer orden; // 1 al 5

    public ResultadoCarrera(ResultadosTest resultadoTest, Carrera carrera, Double porcentaje, Integer orden) {
        this.resultadoTest = resultadoTest;
        this.carrera = carrera;
        this.porcentaje = porcentaje;
        this.orden = orden;
    }
}