package com.vocatio.dto;

public class TestimonioResponseDTO {
    private Long id;
    private String contenido;
    private String nombreAutor;

    public TestimonioResponseDTO() {}

    public TestimonioResponseDTO(Long id, String contenido, String nombreAutor) {
        this.id = id;
        this.contenido = contenido;
        this.nombreAutor = nombreAutor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }
}

