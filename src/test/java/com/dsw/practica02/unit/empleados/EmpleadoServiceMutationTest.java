package com.dsw.practica02.unit.empleados;

import com.dsw.practica02.empleados.dto.EmpleadoPatchRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPutRequest;
import com.dsw.practica02.empleados.exception.BadRequestException;
import com.dsw.practica02.empleados.exception.NotFoundException;
import com.dsw.practica02.empleados.exception.PreconditionFailedException;
import com.dsw.practica02.empleados.exception.PreconditionRequiredException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceMutationTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    @BeforeEach
    void setUp() {
        empleadoService = new EmpleadoServiceImpl(empleadoRepository, new EmpleadoMapper());
    }

    @Test
    void shouldUpdateWithPut() {
        Empleado empleado = new Empleado();
        empleado.setClave("EMP001");
        empleado.setNombre("A");
        empleado.setDireccion("B");
        empleado.setTelefono("C");

        when(empleadoRepository.findById("EMP001")).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("Nuevo", empleadoService.updatePut("EMP001", new EmpleadoPutRequest("Nuevo", "Dir", "Tel"), "0").nombre());
    }

    @Test
    void shouldRejectEmptyPatch() {
        Empleado empleado = new Empleado();
        empleado.setClave("EMP001");
        when(empleadoRepository.findById("EMP001")).thenReturn(Optional.of(empleado));

        assertThrows(BadRequestException.class, () -> empleadoService.updatePatch("EMP001", new EmpleadoPatchRequest(null, null, null), "0"));
    }

    @Test
    void shouldRequireIfMatchOnPut() {
        Empleado empleado = new Empleado();
        empleado.setClave("EMP002");
        when(empleadoRepository.findById("EMP002")).thenReturn(Optional.of(empleado));

        assertThrows(
            PreconditionRequiredException.class,
            () -> empleadoService.updatePut("EMP002", new EmpleadoPutRequest("Nuevo", "Dir", "Tel"), null)
        );
    }

    @Test
    void shouldRejectIfMatchMismatch() {
        Empleado empleado = new Empleado();
        empleado.setClave("EMP003");
        when(empleadoRepository.findById("EMP003")).thenReturn(Optional.of(empleado));

        assertThrows(
            PreconditionFailedException.class,
            () -> empleadoService.updatePatch("EMP003", new EmpleadoPatchRequest("N", null, null), "5")
        );
    }

    @Test
    void shouldThrowNotFoundOnDeleteMissing() {
        when(empleadoRepository.findById("EMPX")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> empleadoService.delete("EMPX"));
    }
}
