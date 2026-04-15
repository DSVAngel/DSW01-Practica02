package com.dsw.practica02.departamentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DepartamentoPatchRequest(
    @Size(max = 100) String nombre,
    @Size(max = 100) String descripcion,
    List<@NotBlank @Size(max = 20) String> empleadosClaves
) {
}
