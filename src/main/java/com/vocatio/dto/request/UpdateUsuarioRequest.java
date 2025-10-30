package com.vocatio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsuarioRequest {

    //Campos obligatorios

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 carácteres e incluir letras y números")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "La contraseña debe tener al menos 8 caracteres e incluir letras y números")
    private String contrasena;

    @NotBlank(message = "El nivel educativo es obligatorio")
    private String nivelEducativo;

    // Campos no obligatorios

    private  String carreraActual;

    private String urlImagenPerfil;

}
