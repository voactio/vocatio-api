package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion_anios", nullable = false)
    private Integer duracionAnios;

    @Column(nullable = false, length = 100)
    private String modalidad;

    // Nuevo: área/interés RIASEC (opcional)
    @Column(name = "perfil_riasec", length = 50)
    private String perfilRiasec;

    @Column(name = "rango_salario_promedio", length = 100)
    private String rangoSalarioPromedio;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getDuracionAnios() { return duracionAnios; }
    public void setDuracionAnios(Integer duracionAnios) { this.duracionAnios = duracionAnios; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getPerfilRiasec() { return perfilRiasec; }
    public void setPerfilRiasec(String perfilRiasec) { this.perfilRiasec = perfilRiasec; }

    public String getRangoSalarioPromedio() { return rangoSalarioPromedio; }
    public void setRangoSalarioPromedio(String rangoSalarioPromedio) { this.rangoSalarioPromedio = rangoSalarioPromedio; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}
