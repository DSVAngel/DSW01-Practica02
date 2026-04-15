package com.dsw.practica02.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpleadoCreateRequest(
    @NotBlank @Size(max = 20) String clave,
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 100) String direccion,
    @NotBlank @Size(max = 100) String telefono
) {
}
