package com.vocatio.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UniversidadCarreraId implements Serializable {

    private Long idUniversidad;
    private Long idCarrera;
}
