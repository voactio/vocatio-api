package com.vocatio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "testimonios")
public class Testimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false, length = 120)
    private String nombreAutor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    public Testimonio() {}

    public Testimonio(String contenido, String nombreAutor, Carrera carrera) {
        this.contenido = contenido;
        this.nombreAutor = nombreAutor;
        this.carrera = carrera;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
}

