package com.dsw.practica02.departamentos.dto;

import java.util.List;

public record DepartamentoPageResponse(
    int page,
    int size,
    long totalElements,
    int totalPages,
    List<DepartamentoResponse> content
) {
}
