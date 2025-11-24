package com.vocatio.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OlvidoRequest {
    @Email(message = "Ingresa un correo válido")
    private String correo;
}
