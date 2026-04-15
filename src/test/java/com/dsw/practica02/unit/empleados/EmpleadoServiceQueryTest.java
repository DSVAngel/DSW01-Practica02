package com.dsw.practica02.unit.empleados;

import com.dsw.practica02.empleados.exception.BadRequestException;
import com.dsw.practica02.empleados.model.Empleado;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import com.dsw.practica02.empleados.service.EmpleadoMapper;
import com.dsw.practica02.empleados.service.EmpleadoServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceQueryTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    @BeforeEach
    void setUp() {
        empleadoService = new EmpleadoServiceImpl(empleadoRepository, new EmpleadoMapper());
    }

    @Test
    void shouldRejectInvalidPagination() {
        assertThrows(BadRequestException.class, () -> empleadoService.list(-1, 10));
        assertThrows(BadRequestException.class, () -> empleadoService.list(0, 0));
        assertThrows(BadRequestException.class, () -> empleadoService.list(0, 51));
    }

    @Test
    void shouldReturnPagedResponse() {
        Empleado empleado = new Empleado();
        empleado.setClave("EMP001");
        empleado.setNombre("Juan");
        empleado.setDireccion("Calle 1");
        empleado.setTelefono("555");

        when(empleadoRepository.findAll(PageRequest.of(0, 10)))
            .thenReturn(new PageImpl<>(List.of(empleado), PageRequest.of(0, 10), 1));

        assertEquals(1, empleadoService.list(0, 10).content().size());
    }
}
