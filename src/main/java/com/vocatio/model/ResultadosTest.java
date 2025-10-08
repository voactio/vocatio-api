package com.vocatio.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "resultados_test")
public class ResultadosTest {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "id_usuario", nullable = false)
    private UUID idUsuario;

    @Column(name = "id_test", nullable = false)
    private UUID idTest;

    @Column(name = "completado_en", nullable = false)
    private OffsetDateTime completadoEn;

    // No necesitamos mapear jsonb para esta funcionalidad.
    @Column(name = "puntajes")
    private String puntajes;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public UUID getIdTest() { return idTest; }
    public void setIdTest(UUID idTest) { this.idTest = idTest; }
    public OffsetDateTime getCompletadoEn() { return completadoEn; }
    public void setCompletadoEn(OffsetDateTime completadoEn) { this.completadoEn = completadoEn; }
    public String getPuntajes() { return puntajes; }
    public void setPuntajes(String puntajes) { this.puntajes = puntajes; }
}
