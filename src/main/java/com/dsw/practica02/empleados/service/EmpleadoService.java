package com.dsw.practica02.empleados.service;

import com.dsw.practica02.empleados.dto.EmpleadoCreateRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPageResponse;
import com.dsw.practica02.empleados.dto.EmpleadoPatchRequest;
import com.dsw.practica02.empleados.dto.EmpleadoPutRequest;
import com.dsw.practica02.empleados.dto.EmpleadoResponse;

public interface EmpleadoService {

    EmpleadoResponse create(EmpleadoCreateRequest request);

    EmpleadoResponse getByClave(String clave);

    EmpleadoPageResponse list(int page, int size);

    EmpleadoResponse updatePut(String clave, EmpleadoPutRequest request, String ifMatch);

    EmpleadoResponse updatePatch(String clave, EmpleadoPatchRequest request, String ifMatch);

    void delete(String clave);
}
