package com.vocatio.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompareCareersRequest {

    private UUID idUsuario;
    private Long idResultado;
    private Long idCarrera1;
    private Long idCarrera2;
}
