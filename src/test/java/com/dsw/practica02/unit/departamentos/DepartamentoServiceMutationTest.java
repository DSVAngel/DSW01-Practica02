package com.dsw.practica02.unit.departamentos;

import com.dsw.practica02.departamentos.dto.DepartamentoPatchRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPutRequest;
import com.dsw.practica02.departamentos.exception.DepartamentoBadRequestException;
import com.dsw.practica02.departamentos.exception.DepartamentoConflictException;
import com.dsw.practica02.departamentos.model.Departamento;
import com.dsw.practica02.departamentos.repository.DepartamentoRepository;
import com.dsw.practica02.departamentos.service.DepartamentoMapper;
import com.dsw.practica02.departamentos.service.DepartamentoServiceImpl;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceMutationTest {

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
    void shouldRejectEmptyPatch() {
        Departamento departamento = new Departamento();
        departamento.setClave("DEP020");

        when(departamentoRepository.findById("DEP020")).thenReturn(Optional.of(departamento));

        assertThrows(
            DepartamentoBadRequestException.class,
            () -> departamentoService.updatePatch("DEP020", new DepartamentoPatchRequest(null, null, null))
        );
    }

    @Test
    void shouldUpdateWithPut() {
        Departamento departamento = new Departamento();
        departamento.setClave("DEP021");
        departamento.setNombre("Anterior");
        departamento.setDescripcion("Anterior");

        when(departamentoRepository.findById("DEP021")).thenReturn(Optional.of(departamento));
        when(departamentoRepository.save(any(Departamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(empleadoRepository.findByDepartamentoClave("DEP021")).thenReturn(List.of());

        DepartamentoPutRequest request = new DepartamentoPutRequest("Nuevo", "Desc", List.of());

        assertEquals("Nuevo", departamentoService.updatePut("DEP021", request).nombre());
    }

    @Test
    void shouldBlockDeleteWhenHasAssociatedEmployees() {
        Departamento departamento = new Departamento();
        departamento.setClave("DEP022");

        when(departamentoRepository.findById("DEP022")).thenReturn(Optional.of(departamento));
        when(empleadoRepository.countByDepartamentoClave("DEP022")).thenReturn(2L);

        assertThrows(DepartamentoConflictException.class, () -> departamentoService.delete("DEP022"));
    }
}
