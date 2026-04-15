package com.dsw.practica02.empleados.service;

import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPageResponse;
import com.dsw.practica02.empleados.dto.EmpleadoPatchRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPutRequest;
import com.dsw.practica02.empleados.dto.EmpleadoResponse;
import com.dsw.practica02.empleados.exception.BadRequestException;
import com.dsw.practica02.empleados.exception.DuplicateKeyException;
import com.dsw.practica02.empleados.exception.NotFoundException;
import com.dsw.practica02.empleados.exception.PreconditionFailedException;
import com.dsw.practica02.empleados.exception.PreconditionRequiredException;
import com.dsw.practica02.empleados.model.Empleado;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoServiceImpl.class);

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, EmpleadoMapper empleadoMapper) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoMapper = empleadoMapper;
    }

    @Override
    public EmpleadoResponse create(EmpleadoCreateRequest request) {
        String clave = request.clave().trim();
        if (empleadoRepository.existsById(clave)) {
            LOGGER.warn("Conflicto de alta por clave duplicada: {}", clave);
            throw new DuplicateKeyException("Ya existe un empleado con la clave " + clave);
        }
        Empleado empleado = empleadoMapper.toEntity(request);
        Empleado saved = empleadoRepository.save(empleado);
        LOGGER.info("Empleado creado: {}", saved.getClave());
        return empleadoMapper.toResponse(saved);
    }

    @Override
    public EmpleadoResponse getByClave(String clave) {
        Empleado empleado = findOrThrow(clave);
        return empleadoMapper.toResponse(empleado);
    }

    @Override
    public EmpleadoPageResponse list(int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new BadRequestException("Parámetros de paginación inválidos");
        }

        Page<Empleado> empleados = empleadoRepository.findAll(PageRequest.of(page, size));
        return new EmpleadoPageResponse(
            empleados.getNumber(),
            empleados.getSize(),
            empleados.getTotalElements(),
            empleados.getTotalPages(),
            empleados.getContent().stream().map(empleadoMapper::toResponse).toList()
        );
    }

    @Override
    public EmpleadoResponse updatePut(String clave, EmpleadoPutRequest request, String ifMatch) {
        Empleado empleado = findOrThrow(clave);
        assertIfMatch(empleado, ifMatch);
        empleado.setNombre(request.nombre().trim());
        empleado.setDireccion(request.direccion().trim());
        empleado.setTelefono(request.telefono().trim());
        Empleado saved = empleadoRepository.save(empleado);
        LOGGER.info("Empleado actualizado (PUT): {}", saved.getClave());
        return empleadoMapper.toResponse(saved);
    }

    @Override
    public EmpleadoResponse updatePatch(String clave, EmpleadoPatchRequest request, String ifMatch) {
        Empleado empleado = findOrThrow(clave);
        assertIfMatch(empleado, ifMatch);

        boolean hasAnyField = request.nombre() != null || request.direccion() != null || request.telefono() != null;
        if (!hasAnyField) {
            throw new BadRequestException("PATCH requiere al menos un campo actualizable");
        }

        if (request.nombre() != null) {
            validateNotBlank(request.nombre(), "nombre");
            empleado.setNombre(request.nombre().trim());
        }
        if (request.direccion() != null) {
            validateNotBlank(request.direccion(), "direccion");
            empleado.setDireccion(request.direccion().trim());
        }
        if (request.telefono() != null) {
            validateNotBlank(request.telefono(), "telefono");
            empleado.setTelefono(request.telefono().trim());
        }

        Empleado saved = empleadoRepository.save(empleado);
        LOGGER.info("Empleado actualizado (PATCH): {}", saved.getClave());
        return empleadoMapper.toResponse(saved);
    }

    @Override
    public void delete(String clave) {
        Empleado empleado = findOrThrow(clave);
        empleadoRepository.delete(empleado);
        LOGGER.info("Empleado eliminado: {}", clave);
    }

    private Empleado findOrThrow(String clave) {
        return empleadoRepository.findById(clave)
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado con clave " + clave));
    }

    private void validateNotBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException("El campo " + field + " no puede estar vacío");
        }
    }

    private void assertIfMatch(Empleado empleado, String ifMatch) {
        if (ifMatch == null || ifMatch.trim().isEmpty()) {
            throw new PreconditionRequiredException("Falta cabecera If-Match");
        }

        long expected = parseIfMatch(ifMatch);
        long current = empleado.getVersion() == null ? 0L : empleado.getVersion();
        if (expected != current) {
            throw new PreconditionFailedException("Conflicto de concurrencia: If-Match no coincide");
        }
    }

    private long parseIfMatch(String ifMatch) {
        String normalized = ifMatch.trim();
        if (normalized.startsWith("W/")) {
            normalized = normalized.substring(2).trim();
        }
        if (normalized.startsWith("\"") && normalized.endsWith("\"") && normalized.length() > 1) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Cabecera If-Match invalida");
        }
    }
}
