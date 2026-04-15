# Implementation Plan: CRUD de Departamentos Conectado a Empleados

**Branch**: `001-crud-departamentos-empleados` | **Date**: 2026-03-08 | **Spec**: [./spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-crud-departamentos-empleados/spec.md`

## Summary

Implementar un CRUD de departamentos integrado con empleados, manteniendo Basic Auth,
persistencia PostgreSQL y contrato OpenAPI protegido. La solucion agrega la entidad
`Departamento`, la relacion 1:N `Departamento -> Empleado`, reglas de asociacion y
reasignacion de empleados, y restriccion de borrado de departamento cuando existan
empleados asociados.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi  
**Storage**: PostgreSQL (tablas `departamentos` y `empleados` con relacion por FK, entorno local Docker)  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test, Testcontainers (PostgreSQL)  
**Target Platform**: Linux (servicio backend HTTP)  
**Project Type**: web-service backend (monolito por capas)  
**Performance Goals**: p95 menor a 2 segundos para operaciones CRUD y asociacion en pruebas de integracion  
**Constraints**: Basic Auth obligatoria, validaciones de longitud y no vacio, integridad referencial entre departamento y empleado, Swagger protegido  
**Scale/Scope**: 1 agregado nuevo (`Departamento`), extension del agregado `Empleado`, 6 endpoints CRUD de departamentos + ajustes de consulta para reflejar asociacion

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Stack gate: PASS - implementacion backend en Spring Boot 3 + Java 17.
- Security gate: PASS - autenticacion basica requerida para endpoints y documentacion.
- Data gate: PASS - PostgreSQL con configuracion externalizada y soporte Docker local.
- API docs gate: PASS - contrato OpenAPI definido para nuevos endpoints y relacion con empleados.
- Quality gate: PASS - estrategia de pruebas unitarias e integracion definida para reglas criticas.
- Observability gate: PASS - se mantiene logging para seguridad y errores de persistencia.

**Resultado Pre-Phase 0**: PASS.

## Project Structure

### Documentation (this feature)

```text
specs/001-crud-departamentos-empleados/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── departamentos.openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
└── main/
  ├── java/
  │   └── com/dsw/practica02/
  │       ├── config/
  │       ├── empleados/
  │       │   ├── controller/
  │       │   ├── dto/
  │       │   ├── model/
  │       │   ├── repository/
  │       │   └── service/
  │       └── departamentos/
  │           ├── controller/
  │           ├── dto/
  │           ├── model/
  │           ├── repository/
  │           └── service/
  └── resources/
    └── application.properties

src/test/
└── java/
  └── com/dsw/practica02/
    ├── unit/
    │   ├── empleados/
    │   └── departamentos/
    └── integration/
      ├── empleados/
      └── departamentos/

docker/
└── docker-compose.yml
```

**Structure Decision**: Se mantiene un unico backend Spring Boot con arquitectura por
capas y separacion por dominio (`empleados`, `departamentos`), reutilizando patrones
existentes de controller/service/repository/dto y base de pruebas Testcontainers.

## Constitution Check (Post-Design)

- Stack gate: PASS - diseno alineado a Spring Boot 3 + Java 17.
- Security gate: PASS - contrato y quickstart definen Basic Auth para todas las rutas.
- Data gate: PASS - modelo relacional y restricciones de integridad definidos sobre PostgreSQL.
- API docs gate: PASS - `contracts/departamentos.openapi.yaml` cubre CRUD y vinculacion con empleados.
- Quality gate: PASS - quickstart y research establecen pruebas unitarias e integracion para casos clave.
- Observability gate: PASS - se contempla mantener logging en servicio para conflictos, not found y errores de asociacion.

**Resultado Post-Phase 1**: PASS.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
