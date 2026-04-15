package com.dsw.practica02.unit.departamentos;

import com.dsw.practica02.departamentos.dto.DepartamentoCreateRequest;
import com.dsw.practica02.departamentos.exception.DepartamentoBadRequestException;
import com.dsw.practica02.departamentos.exception.DepartamentoDuplicateKeyException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceCreateQueryTest {

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
    void shouldCreateDepartamentoWhenClaveIsUnique() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest("DEP001", "Ventas", "Comercial", List.of());
        Departamento saved = new Departamento();
        saved.setClave("DEP001");
        saved.setNombre("Ventas");
        saved.setDescripcion("Comercial");

        when(departamentoRepository.existsById("DEP001")).thenReturn(false);
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(saved);
        when(empleadoRepository.findByDepartamentoClave("DEP001")).thenReturn(List.of());

        assertEquals("DEP001", departamentoService.create(request).clave());
    }

    @Test
    void shouldThrowConflictWhenClaveAlreadyExists() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest("DEP001", "Ventas", "Comercial", List.of());
        when(departamentoRepository.existsById("DEP001")).thenReturn(true);

        assertThrows(DepartamentoDuplicateKeyException.class, () -> departamentoService.create(request));
    }

    @Test
    void shouldRejectInvalidPagination() {
        assertThrows(DepartamentoBadRequestException.class, () -> departamentoService.list(-1, 10));
        assertThrows(DepartamentoBadRequestException.class, () -> departamentoService.list(0, 0));
        assertThrows(DepartamentoBadRequestException.class, () -> departamentoService.list(0, 51));
    }

    @Test
    void shouldReturnPagedDepartamentoResponse() {
        Departamento departamento = new Departamento();
        departamento.setClave("DEP002");
        departamento.setNombre("Sistemas");
        departamento.setDescripcion("TI");

        when(departamentoRepository.findAll(PageRequest.of(0, 10)))
            .thenReturn(new PageImpl<>(List.of(departamento), PageRequest.of(0, 10), 1));
        when(empleadoRepository.findByDepartamentoClave("DEP002")).thenReturn(List.of());

        assertEquals(1, departamentoService.list(0, 10).content().size());
    }
}
