package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "resultados_test",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_test", "intento"})
)
public class ResultadosTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_test", nullable = false)
    private Long idTest;

    @Column(nullable = false)
    private Integer intento;

    @Column(name = "completado_en", nullable = false)
    private LocalDateTime completadoEn;

    @Column(name = "puntajes", columnDefinition = "jsonb", nullable = false)
    private String puntajes;
}
