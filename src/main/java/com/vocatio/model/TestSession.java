package com.vocatio.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "test_sessions")
@Data
public class TestSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_test")
    private Test test;

    private String estado;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<Long, Long> respuestas = new HashMap<>();

    private LocalDateTime iniciadoEn = LocalDateTime.now();
    private LocalDateTime completadoEn;
}