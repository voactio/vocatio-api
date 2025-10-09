package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "universidades")
@Data
public class Universidad {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String ubicacion;              // city

    // "public" / "private" según tu carga
    private String tipo;

    @Column(name = "url_sitio_web")
    private String urlSitioWeb;
}
