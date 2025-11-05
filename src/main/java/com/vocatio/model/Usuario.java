package com.vocatio.model;

//Importaciones
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

//Entidad JPA, es una tabla en BaseDatos
//  nombre tabla: users
//  restriccion UNICO: no puede haber correos duplicados
@Entity
@Getter
@Setter
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "correo"))
public class Usuario {

    //Clave primaria
    //  activa automaticamente el autoincremento
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    private String nivelEducativo;

    @ManyToOne
    @JoinColumn(name = "carrera_id", nullable = true)
    private Carrera carrera;

    private String urlImagenPerfil;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    private LocalDateTime actualizadoEn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private Boolean active = true;
}
