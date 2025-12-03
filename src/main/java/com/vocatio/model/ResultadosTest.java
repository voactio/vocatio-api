package com.vocatio.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
    private UUID idUsuario;

    @Column(name = "id_test", nullable = false)
    private Long idTest;

    @Column(nullable = false)
    private Integer intento;

    @Column(name = "completado_en", nullable = false)
    private LocalDateTime completadoEn;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "puntajes", columnDefinition = "jsonb", nullable = false)
    private Map<String, Integer> puntajes;
}