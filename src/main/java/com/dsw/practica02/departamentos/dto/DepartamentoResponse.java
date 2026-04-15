package com.dsw.practica02.departamentos.dto;

import java.util.List;

public record DepartamentoResponse(
    String clave,
    String nombre,
    String descripcion,
    List<EmpleadoResumen> empleados
) {
}
