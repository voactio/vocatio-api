package com.vocatio.dto.request;

// Validar auto los datos
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;   // genera metodos GET
import lombok.Setter;   // genera metodos SET

@Getter
@Setter
public class RegisterUsuarioRequest {

    // Campos obligatorios
    @NotBlank(message = "El nombre es obligatorio")
    @JsonProperty("nombre")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener almenos 8 caracteres e incluir letras y números")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe tener almenos 8 caracteres e incluir letras y números")
    private String contrasena;

    @NotBlank(message = "El nivel educativo es obligatorio")
    private String nivelEducativo;

    // Campos opcionales
    private String carreraActual;

    private String urlImagenPerfil;
}
