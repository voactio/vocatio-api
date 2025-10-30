package com.vocatio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "areas_interes")
@Data
public class AreasInteres {
    @Id
    private String id; // "R", "I", "A", "S", "E", "C"
    private String nombre;
    private String descripcion;
}