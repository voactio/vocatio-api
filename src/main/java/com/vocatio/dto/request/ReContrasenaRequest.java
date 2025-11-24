package com.vocatio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReContrasenaRequest {
    private String token;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener almenos 8 caracteres e incluir letras y números")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe tener almenos 8 caracteres e incluir letras y números")
    private String nuevaContrasena;

    private String confirmarContrasena;
}
