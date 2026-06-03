package com.dsw.practica02.empleados.controller;

import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPageResponse;
import com.dsw.practica02.empleados.dto.EmpleadoPatchRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPutRequest;
import com.dsw.practica02.empleados.dto.EmpleadoResponse;
import com.dsw.practica02.empleados.service.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empleados")
@Validated
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoResponse create(@RequestBody @Valid EmpleadoCreateRequest request) {
        return empleadoService.create(request);
    }

    @GetMapping("/{clave}")
    public ResponseEntity<EmpleadoResponse> getByClave(@PathVariable String clave) {
        EmpleadoResponse response = empleadoService.getByClave(clave);
        return ResponseEntity.ok()
            .eTag(toEtag(response.version()))
            .body(response);
    }

    @GetMapping
    public EmpleadoPageResponse list(
        @RequestParam @Min(0) int page,
        @RequestParam @Min(1) @Max(50) int size
    ) {
        return empleadoService.list(page, size);
    }

    @PutMapping("/{clave}")
    public ResponseEntity<EmpleadoResponse> updatePut(
        @PathVariable String clave,
        @RequestBody @Valid EmpleadoPutRequest request,
        @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        EmpleadoResponse response = empleadoService.updatePut(clave, request, ifMatch);
        return ResponseEntity.ok()
            .eTag(toEtag(response.version()))
            .body(response);
    }

    @PatchMapping("/{clave}")
    public ResponseEntity<EmpleadoResponse> updatePatch(
        @PathVariable String clave,
        @RequestBody @Valid EmpleadoPatchRequest request,
        @RequestHeader(value = "If-Match", required = false) String ifMatch
    ) {
        EmpleadoResponse response = empleadoService.updatePatch(clave, request, ifMatch);
        return ResponseEntity.ok()
            .eTag(toEtag(response.version()))
            .body(response);
    }

    @DeleteMapping("/{clave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String clave) {
        empleadoService.delete(clave);
    }

    private String toEtag(Long version) {
        long safeVersion = version == null ? 0L : version;
        return "\"" + safeVersion + "\"";
    }
}
