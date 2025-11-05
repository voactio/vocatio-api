// src/main/java/com/vocatio/model/Testimonio.java
package com.vocatio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "testimonios")
public class Testimonio {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrera", nullable = false)
    private Carrera carrera;

    @Column(name = "id_usuario", nullable = false)
    private UUID idUsuario;

    @Column(name = "texto_testimonio", nullable = false, columnDefinition = "TEXT")
    private String textoTestimonio;

    @Column(nullable = false)
    private boolean aprobado;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        this.creadoEn = LocalDateTime.now();
    }

    public String getId() { return id; }
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public String getTextoTestimonio() { return textoTestimonio; }
    public void setTextoTestimonio(String textoTestimonio) { this.textoTestimonio = textoTestimonio; }
    public boolean isAprobado() { return aprobado; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
