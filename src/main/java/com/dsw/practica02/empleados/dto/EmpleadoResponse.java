package com.dsw.practica02.empleados.dto;

import com.dsw.practica02.departamentos.dto.DepartamentoResumen;

public record EmpleadoResponse(
    String clave,
    String nombre,
    String direccion,
    String telefono,
    DepartamentoResumen departamento,
    Long version
) {
}
