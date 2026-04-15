package com.dsw.practica02.departamentos.controller;

import com.dsw.practica02.departamentos.dto.DepartamentoCreateRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPageResponse;
import com.dsw.practica02.departamentos.dto.DepartamentoPatchRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPutRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoResponse;
import com.dsw.practica02.departamentos.service.DepartamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departamentos")
@Validated
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoResponse create(@RequestBody @Valid DepartamentoCreateRequest request) {
        return departamentoService.create(request);
    }

    @GetMapping("/{clave}")
    public DepartamentoResponse getByClave(@PathVariable String clave) {
        return departamentoService.getByClave(clave);
    }

    @GetMapping
    public DepartamentoPageResponse list(
        @RequestParam @Min(0) int page,
        @RequestParam @Min(1) @Max(50) int size
    ) {
        return departamentoService.list(page, size);
    }

    @PutMapping("/{clave}")
    public DepartamentoResponse updatePut(@PathVariable String clave, @RequestBody @Valid DepartamentoPutRequest request) {
        return departamentoService.updatePut(clave, request);
    }

    @PatchMapping("/{clave}")
    public DepartamentoResponse updatePatch(@PathVariable String clave, @RequestBody @Valid DepartamentoPatchRequest request) {
        return departamentoService.updatePatch(clave, request);
    }

    @DeleteMapping("/{clave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String clave) {
        departamentoService.delete(clave);
    }
}
