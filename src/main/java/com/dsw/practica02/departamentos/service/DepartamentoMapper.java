package com.dsw.practica02.departamentos.service;

import com.dsw.practica02.departamentos.dto.DepartamentoCreateRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoResponse;
import com.dsw.practica02.departamentos.dto.DepartamentoResumen;
import com.dsw.practica02.departamentos.dto.EmpleadoResumen;
import com.dsw.practica02.departamentos.model.Departamento;
import com.dsw.practica02.empleados.model.Empleado;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartamentoMapper {

    public Departamento toEntity(DepartamentoCreateRequest request) {
        Departamento departamento = new Departamento();
        departamento.setClave(request.clave().trim());
        departamento.setNombre(request.nombre().trim());
        departamento.setDescripcion(request.descripcion().trim());
        return departamento;
    }

    public DepartamentoResumen toResumen(Departamento departamento) {
        if (departamento == null) {
            return null;
        }
        return new DepartamentoResumen(departamento.getClave(), departamento.getNombre());
    }

    public DepartamentoResponse toResponse(Departamento departamento, List<Empleado> empleadosAsociados) {
        List<EmpleadoResumen> empleados = empleadosAsociados.stream()
            .map(this::toEmpleadoResumen)
            .toList();

        return new DepartamentoResponse(
            departamento.getClave(),
            departamento.getNombre(),
            departamento.getDescripcion(),
            empleados
        );
    }

    private EmpleadoResumen toEmpleadoResumen(Empleado empleado) {
        return new EmpleadoResumen(empleado.getClave(), empleado.getNombre());
    }
}
