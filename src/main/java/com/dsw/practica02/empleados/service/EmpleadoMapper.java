package com.dsw.practica02.empleados.service;

import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.dto.EmpleadoResponse;
import com.dsw.practica02.departamentos.dto.DepartamentoResumen;
import com.dsw.practica02.empleados.model.Empleado;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {

    public Empleado toEntity(EmpleadoCreateRequest request) {
        Empleado empleado = new Empleado();
        empleado.setClave(request.clave().trim());
        empleado.setNombre(request.nombre().trim());
        empleado.setDireccion(request.direccion().trim());
        empleado.setTelefono(request.telefono().trim());
        return empleado;
    }

    public EmpleadoResponse toResponse(Empleado empleado) {
        DepartamentoResumen departamento = null;
        if (empleado.getDepartamento() != null) {
            departamento = new DepartamentoResumen(
                empleado.getDepartamento().getClave(),
                empleado.getDepartamento().getNombre()
            );
        }

        return new EmpleadoResponse(
            empleado.getClave(),
            empleado.getNombre(),
            empleado.getDireccion(),
            empleado.getTelefono(),
            departamento,
            empleado.getVersion()
        );
    }
}
