package com.dsw.practica02.unit.empleados;

import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.exception.DuplicateKeyException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceCreateTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    @BeforeEach
    void setUp() {
        empleadoService = new EmpleadoServiceImpl(empleadoRepository, new EmpleadoMapper());
    }

    @Test
    void shouldCreateEmpleadoWhenClaveIsUnique() {
        EmpleadoCreateRequest request = new EmpleadoCreateRequest("EMP001", "Juan", "Calle 1", "555");
        Empleado saved = new Empleado();
        saved.setClave("EMP001");
        saved.setNombre("Juan");
        saved.setDireccion("Calle 1");
        saved.setTelefono("555");

        when(empleadoRepository.existsById("EMP001")).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(saved);

        assertEquals("EMP001", empleadoService.create(request).clave());
    }

    @Test
    void shouldThrowConflictWhenClaveAlreadyExists() {
        EmpleadoCreateRequest request = new EmpleadoCreateRequest("EMP001", "Juan", "Calle 1", "555");
        when(empleadoRepository.existsById("EMP001")).thenReturn(true);

        assertThrows(DuplicateKeyException.class, () -> empleadoService.create(request));
    }
}
