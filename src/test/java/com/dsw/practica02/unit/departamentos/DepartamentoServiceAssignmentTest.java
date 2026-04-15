package com.dsw.practica02.unit.departamentos;

import com.dsw.practica02.departamentos.dto.DepartamentoPutRequest;
import com.dsw.practica02.departamentos.exception.DepartamentoBadRequestException;
import com.dsw.practica02.departamentos.exception.DepartamentoNotFoundException;
import com.dsw.practica02.departamentos.model.Departamento;
import com.dsw.practica02.departamentos.repository.DepartamentoRepository;
import com.dsw.practica02.departamentos.service.DepartamentoMapper;
import com.dsw.practica02.departamentos.service.DepartamentoServiceImpl;
import com.dsw.practica02.empleados.model.Empleado;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceAssignmentTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private DepartamentoServiceImpl departamentoService;

    @BeforeEach
    void setUp() {
        departamentoService = new DepartamentoServiceImpl(
            departamentoRepository,
            empleadoRepository,
            new DepartamentoMapper()
        );
    }

    @Test
    void shouldRejectDuplicatedEmployeeKeysOnAssignment() {
        Departamento departamento = new Departamento();
        departamento.setClave("DEP010");
        departamento.setNombre("Operaciones");
        departamento.setDescripcion("Ops");

        when(departamentoRepository.findById("DEP010")).thenReturn(Optional.of(departamento));
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(departamento);
        when(empleadoRepository.findByDepartamentoClave("DEP010")).thenReturn(List.of());

        DepartamentoPutRequest request = new DepartamentoPutRequest(
            "Operaciones",
            "Ops",
            List.of("EMP001", "EMP001")
        );

        assertThrows(DepartamentoBadRequestException.class, () -> departamentoService.updatePut("DEP010", request));
    }

    @Test
    void shouldRejectMissingEmployeesOnAssignment() {
        Departamento departamento = new Departamento();
        departamento.setClave("DEP011");
        departamento.setNombre("Finanzas");
        departamento.setDescripcion("Fin");

        Empleado empleado = new Empleado();
        empleado.setClave("EMP001");

        when(departamentoRepository.findById("DEP011")).thenReturn(Optional.of(departamento));
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(departamento);
        when(empleadoRepository.findByDepartamentoClave("DEP011")).thenReturn(List.of());
        when(empleadoRepository.findByClaveIn(List.of("EMP001", "EMP999"))).thenReturn(List.of(empleado));

        DepartamentoPutRequest request = new DepartamentoPutRequest(
            "Finanzas",
            "Fin",
            List.of("EMP001", "EMP999")
        );

        assertThrows(DepartamentoNotFoundException.class, () -> departamentoService.updatePut("DEP011", request));
    }
}
