package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "universidades")
@Data
@Getter
@Setter
public class Universidad {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String ubicacion;

    private String tipo;

    @Column(name = "url_sitio_web")
    private String urlSitioWeb;
}
