package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recursos")
@Getter @Setter
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(name = "tipo_recurso")
    private String tipoRecurso;
    private String url;
    private String autor;
}
