// src/main/java/com/vocatio/dto/response/TestimonioResponse.java
package com.vocatio.dto.response;

import java.time.LocalDateTime;

public class TestimonioResponse {
    private String id;
    private String idUsuario;
    private Long idCarrera;
    private String textoTestimonio;
    private boolean aprobado;
    private LocalDateTime creadoEn;

    public TestimonioResponse(String id, String idUsuario, Long idCarrera,
                              String textoTestimonio, boolean aprobado, LocalDateTime creadoEn) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idCarrera = idCarrera;
        this.textoTestimonio = textoTestimonio;
        this.aprobado = aprobado;
        this.creadoEn = creadoEn;
    }

    public String getId() { return id; }
    public String getIdUsuario() { return idUsuario; }
    public Long getIdCarrera() { return idCarrera; }
    public String getTextoTestimonio() { return textoTestimonio; }
    public boolean isAprobado() { return aprobado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
