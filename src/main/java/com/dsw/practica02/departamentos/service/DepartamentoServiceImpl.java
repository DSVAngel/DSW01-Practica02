package com.dsw.practica02.departamentos.service;

import com.dsw.practica02.departamentos.dto.DepartamentoCreateRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPageResponse;
import com.dsw.practica02.departamentos.dto.DepartamentoPatchRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPutRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoResponse;
import com.dsw.practica02.departamentos.exception.DepartamentoBadRequestException;
import com.dsw.practica02.departamentos.exception.DepartamentoConflictException;
import com.dsw.practica02.departamentos.exception.DepartamentoDuplicateKeyException;
import com.dsw.practica02.departamentos.exception.DepartamentoNotFoundException;
import com.dsw.practica02.departamentos.model.Departamento;
import com.dsw.practica02.departamentos.repository.DepartamentoRepository;
import com.dsw.practica02.empleados.model.Empleado;
import com.dsw.practica02.empleados.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class DepartamentoServiceImpl implements DepartamentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartamentoServiceImpl.class);

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoMapper departamentoMapper;

    public DepartamentoServiceImpl(
        DepartamentoRepository departamentoRepository,
        EmpleadoRepository empleadoRepository,
        DepartamentoMapper departamentoMapper
    ) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.departamentoMapper = departamentoMapper;
    }

    @Override
    public DepartamentoResponse create(DepartamentoCreateRequest request) {
        String clave = normalizeRequired(request.clave(), "clave");
        if (departamentoRepository.existsById(clave)) {
            LOGGER.warn("Conflicto de alta por clave duplicada de departamento: {}", clave);
            throw new DepartamentoDuplicateKeyException("Ya existe un departamento con la clave " + clave);
        }

        Departamento departamento = departamentoMapper.toEntity(request);
        departamento.setClave(clave);
        departamento.setNombre(normalizeRequired(request.nombre(), "nombre"));
        departamento.setDescripcion(normalizeRequired(request.descripcion(), "descripcion"));

        Departamento saved = departamentoRepository.save(departamento);
        replaceAssociations(saved, request.empleadosClaves());
        LOGGER.info("Departamento creado: {}", saved.getClave());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartamentoResponse getByClave(String clave) {
        return toResponse(findOrThrow(clave));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartamentoPageResponse list(int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new DepartamentoBadRequestException("Parametros de paginacion invalidos");
        }

        Page<Departamento> departamentos = departamentoRepository.findAll(PageRequest.of(page, size));
        return new DepartamentoPageResponse(
            departamentos.getNumber(),
            departamentos.getSize(),
            departamentos.getTotalElements(),
            departamentos.getTotalPages(),
            departamentos.getContent().stream().map(this::toResponse).toList()
        );
    }

    @Override
    public DepartamentoResponse updatePut(String clave, DepartamentoPutRequest request) {
        Departamento departamento = findOrThrow(clave);
        departamento.setNombre(normalizeRequired(request.nombre(), "nombre"));
        departamento.setDescripcion(normalizeRequired(request.descripcion(), "descripcion"));
        Departamento saved = departamentoRepository.save(departamento);

        replaceAssociations(saved, request.empleadosClaves());
        LOGGER.info("Departamento actualizado (PUT): {}", saved.getClave());
        return toResponse(saved);
    }

    @Override
    public DepartamentoResponse updatePatch(String clave, DepartamentoPatchRequest request) {
        Departamento departamento = findOrThrow(clave);

        boolean hasAnyField = request.nombre() != null
            || request.descripcion() != null
            || request.empleadosClaves() != null;
        if (!hasAnyField) {
            throw new DepartamentoBadRequestException("PATCH requiere al menos un campo actualizable");
        }

        if (request.nombre() != null) {
            departamento.setNombre(normalizeRequired(request.nombre(), "nombre"));
        }
        if (request.descripcion() != null) {
            departamento.setDescripcion(normalizeRequired(request.descripcion(), "descripcion"));
        }
        Departamento saved = departamentoRepository.save(departamento);

        if (request.empleadosClaves() != null) {
            replaceAssociations(saved, request.empleadosClaves());
            LOGGER.info("Departamento actualizado (PATCH) con reasignacion de empleados: {}", saved.getClave());
        } else {
            LOGGER.info("Departamento actualizado (PATCH): {}", saved.getClave());
        }

        return toResponse(saved);
    }

    @Override
    public void delete(String clave) {
        Departamento departamento = findOrThrow(clave);
        long asociados = empleadoRepository.countByDepartamentoClave(departamento.getClave());
        if (asociados > 0) {
            LOGGER.warn("Intento de eliminar departamento con empleados asociados: {}", clave);
            throw new DepartamentoConflictException("No se puede eliminar un departamento con empleados asociados");
        }

        departamentoRepository.delete(departamento);
        LOGGER.info("Departamento eliminado: {}", clave);
    }

    private Departamento findOrThrow(String clave) {
        return departamentoRepository.findById(clave)
            .orElseThrow(() -> new DepartamentoNotFoundException("Departamento no encontrado con clave " + clave));
    }

    private String normalizeRequired(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new DepartamentoBadRequestException("El campo " + field + " no puede estar vacio");
        }
        return value.trim();
    }

    private List<String> normalizeEmployeeClaves(List<String> claves) {
        if (claves == null) {
            return List.of();
        }

        List<String> normalized = new ArrayList<>();
        Set<String> unique = new LinkedHashSet<>();

        for (String clave : claves) {
            String normalizedClave = normalizeRequired(clave, "empleadosClaves");
            normalized.add(normalizedClave);
            unique.add(normalizedClave);
        }

        if (unique.size() != normalized.size()) {
            throw new DepartamentoBadRequestException("No se permiten claves de empleados duplicadas en la misma solicitud");
        }

        return normalized;
    }

    private void replaceAssociations(Departamento departamento, List<String> empleadosClaves) {
        List<Empleado> actuales = empleadoRepository.findByDepartamentoClave(departamento.getClave());
        if (!actuales.isEmpty()) {
            actuales.forEach(empleado -> empleado.setDepartamento(null));
            empleadoRepository.saveAll(actuales);
        }

        List<String> normalizedClaves = normalizeEmployeeClaves(empleadosClaves);
        if (normalizedClaves.isEmpty()) {
            return;
        }

        List<Empleado> empleados = empleadoRepository.findByClaveIn(normalizedClaves);
        if (empleados.size() != normalizedClaves.size()) {
            Set<String> found = empleados.stream().map(Empleado::getClave).collect(LinkedHashSet::new, Set::add, Set::addAll);
            List<String> missing = normalizedClaves.stream().filter(clave -> !found.contains(clave)).toList();
            throw new DepartamentoNotFoundException("Empleado(s) no encontrado(s): " + String.join(",", missing));
        }

        empleados.forEach(empleado -> empleado.setDepartamento(departamento));
        empleadoRepository.saveAll(empleados);
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        List<Empleado> empleadosAsociados = empleadoRepository.findByDepartamentoClave(departamento.getClave());
        return departamentoMapper.toResponse(departamento, empleadosAsociados);
    }
}
