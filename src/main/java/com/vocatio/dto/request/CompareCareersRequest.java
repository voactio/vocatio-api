package com.vocatio.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompareCareersRequest {

    private Long idUsuario;
    private Long idResultado;
    private Long idCarrera1;
    private Long idCarrera2;
}
