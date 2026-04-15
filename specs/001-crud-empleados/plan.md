# Implementation Plan: CRUD de Empleados (Backend + Frontend)

**Branch**: `001-crud-departamentos-empleados` | **Date**: 2026-03-25 | **Spec**: `/specs/001-crud-empleados/spec.md`
**Input**: Feature specification from `/specs/001-crud-empleados/spec.md`

## Summary

Implementar un CRUD full-stack de empleados en monorepo: backend Spring Boot 3 + Java 17
con PostgreSQL y frontend Angular 21 consumiendo contrato OpenAPI. Se incorpora control
optimista de concurrencia con `ETag` + `If-Match` (`412` en mismatch, `428` si falta
precondicion) y autenticacion basica ingresada manualmente en frontend, sin persistencia
de credenciales.

## Technical Context

**Language/Version**: Java 17 (backend), TypeScript + Angular 21 (frontend)  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi, Angular 21  
**Storage**: PostgreSQL (tabla `empleados`)  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc (backend), Angular test runner + E2E para flujo critico (frontend)  
**Target Platform**: Linux local con Docker para DB, API en puerto 8080 y cliente web Angular
**Project Type**: Monorepo web app (backend API + frontend SPA)  
**Performance Goals**: >=95% de operaciones CRUD bajo 2s en pruebas de integracion  
**Constraints**: Basic Auth obligatoria, `clave` 1..20, campos texto 1..100, paginacion `size` 1..50, control optimista con `ETag/If-Match`  
**Scale/Scope**: Una entidad de dominio (`Empleado`) con CRUD completo, cliente frontend y manejo de conflictos de concurrencia

## Constitution Check (Pre-Design)

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Stack gate: PASS - backend en Spring Boot 3 + Java 17 y frontend en Angular 21.
- Security gate: PASS - Basic Auth para API/docs; frontend con login manual y sin secretos persistidos.
- Contract gate: PASS - OpenAPI definido como fuente de verdad con sincronizacion backend/frontend.
- Data gate: PASS - PostgreSQL y configuracion externalizada con Docker local.
- Quality gate: PASS - planifica pruebas backend (unit/integration) y frontend (component/E2E).
- Observability gate: PASS - logging de errores de seguridad, validacion, persistencia y concurrencia.

## Project Structure

### Documentation (this feature)

```text
specs/001-crud-empleados/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── empleados.openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/
│   └── resources/

test/
└── java/

frontend/
├── src/
├── e2e/
└── package.json

docker/
```

**Structure Decision**: Se mantiene backend Java existente y se incorpora/actualiza
`frontend/` para Angular 21, con contrato OpenAPI compartido como frontera de integracion.

## Phase 0 Research Output

- Persistencia de empleados en PostgreSQL con PK de negocio `clave`.
- Seguridad de API bajo Basic Auth y login manual de frontend sin almacenamiento persistente.
- Contrato OpenAPI como fuente de verdad para consumo Angular.
- Concurrencia optimista via `ETag` + `If-Match` con codigos `412` y `428`.
- Estrategia de pruebas full-stack (backend + frontend).

## Phase 1 Design Output

- `data-model.md` con entidad `Empleado`, metadato de version y estados de concurrencia.
- `contracts/empleados.openapi.yaml` con requisitos de cabeceras `ETag`/`If-Match` y errores `412`/`428`.
- `quickstart.md` con flujo local de backend/frontend y pruebas de concurrencia.

## Constitution Check (Post-Design)

- Stack gate: PASS - diseno final cubre backend y frontend constitucionales.
- Security gate: PASS - autenticacion basica en API/docs y frontend sin secretos hardcodeados.
- Contract gate: PASS - contrato versionado y sincronizado con reglas de concurrencia.
- Data gate: PASS - persistencia PostgreSQL alineada con restricciones y versionado de recurso.
- Quality gate: PASS - criterios de pruebas backend/frontend y casos E2E definidos.
- Observability gate: PASS - plan de logging incluye conflictos de concurrencia y fallos de validacion.

## Complexity Tracking

Sin violaciones constitucionales abiertas.
