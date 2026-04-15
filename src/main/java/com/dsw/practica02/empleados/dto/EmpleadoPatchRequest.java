package com.dsw.practica02.empleados.dto;

import jakarta.validation.constraints.Size;

public record EmpleadoPatchRequest(
    @Size(max = 100) String nombre,
    @Size(max = 100) String direccion,
    @Size(max = 100) String telefono
) {
}
