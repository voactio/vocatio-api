package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "preguntas")
@Data
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String textoPregunta;
    private int orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_test")
    private Test test;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL)
    private List<Opcion> opciones;
}