package com.dsw.practica02.departamentos.service;

import com.dsw.practica02.departamentos.dto.DepartamentoCreateRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPageResponse;
import com.dsw.practica02.departamentos.dto.DepartamentoPatchRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoPutRequest;
import com.dsw.practica02.departamentos.dto.DepartamentoResponse;

public interface DepartamentoService {

    DepartamentoResponse create(DepartamentoCreateRequest request);

    DepartamentoResponse getByClave(String clave);

    DepartamentoPageResponse list(int page, int size);

    DepartamentoResponse updatePut(String clave, DepartamentoPutRequest request);

    DepartamentoResponse updatePatch(String clave, DepartamentoPatchRequest request);

    void delete(String clave);
}
