package com.dsw.practica02.empleados.repository;

import com.dsw.practica02.empleados.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

	List<Empleado> findByDepartamentoClave(String departamentoClave);

	long countByDepartamentoClave(String departamentoClave);

	List<Empleado> findByClaveIn(List<String> claves);
}
