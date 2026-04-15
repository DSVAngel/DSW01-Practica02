package com.dsw.practica02.empleados.dto;

import java.util.List;

public record EmpleadoPageResponse(
    int page,
    int size,
    long totalElements,
    int totalPages,
    List<EmpleadoResponse> content
) {
}
