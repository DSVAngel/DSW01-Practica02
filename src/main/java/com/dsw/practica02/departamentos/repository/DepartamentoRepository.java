package com.dsw.practica02.departamentos.repository;

import com.dsw.practica02.departamentos.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, String> {
}
