# Tasks: CRUD de Departamentos Conectado a Empleados

**Input**: Design documents from `/specs/001-crud-departamentos-empleados/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Las pruebas son obligatorias por constitucion para autenticacion, persistencia y endpoints criticos.

**Organization**: Tareas agrupadas por historia de usuario para implementacion y validacion independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar estructura base de codigo y pruebas para el dominio de departamentos.

- [X] T001 Crear estructura del dominio departamentos en `src/main/java/com/dsw/practica02/departamentos/`
- [X] T002 Crear estructura de pruebas unitarias de departamentos en `src/test/java/com/dsw/practica02/unit/departamentos/`
- [X] T003 [P] Crear estructura de pruebas de integracion de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/`
- [X] T004 [P] Crear base Testcontainers para integracion de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/BasePostgresDepartamentoIntegrationTest.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura bloqueante requerida antes de iniciar cualquier historia.

**CRITICAL**: Ninguna historia de usuario debe iniciar sin completar esta fase.

- [X] T005 Verificar configuracion Docker/PostgreSQL del feature en `docker/docker-compose.yml`
- [X] T006 Verificar propiedades de datasource y JPA para relacion con departamentos en `src/main/resources/application.properties`
- [X] T007 Ajustar seguridad para asegurar autenticacion en endpoints de departamentos en `src/main/java/com/dsw/practica02/config/SecurityConfig.java`
- [X] T008 [P] Preparar contrato OpenAPI base del feature en `specs/001-crud-departamentos-empleados/contracts/departamentos.openapi.yaml`
- [X] T009 Crear entidad Departamento con restricciones JPA en `src/main/java/com/dsw/practica02/departamentos/model/Departamento.java`
- [X] T010 Modificar entidad Empleado para relacion ManyToOne con Departamento en `src/main/java/com/dsw/practica02/empleados/model/Empleado.java`
- [X] T011 [P] Crear repositorio DepartamentoRepository en `src/main/java/com/dsw/practica02/departamentos/repository/DepartamentoRepository.java`
- [X] T012 [P] Extender EmpleadoRepository con consultas por departamento en `src/main/java/com/dsw/practica02/empleados/repository/EmpleadoRepository.java`
- [X] T013 [P] Crear excepciones de dominio de departamentos en `src/main/java/com/dsw/practica02/departamentos/exception/`
- [X] T014 Integrar excepciones de departamentos en GlobalExceptionHandler en `src/main/java/com/dsw/practica02/config/GlobalExceptionHandler.java`
- [X] T015 [P] Crear DTOs de resumen y paginacion de departamentos en `src/main/java/com/dsw/practica02/departamentos/dto/`
- [X] T016 Extender EmpleadoResponse con resumen de departamento en `src/main/java/com/dsw/practica02/empleados/dto/EmpleadoResponse.java`
- [X] T017 Crear DepartamentoMapper para entidad y DTOs en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoMapper.java`
- [X] T018 Actualizar EmpleadoMapper para mapear resumen de departamento en `src/main/java/com/dsw/practica02/empleados/service/EmpleadoMapper.java`
- [X] T019 Crear interfaz DepartamentoService con operaciones CRUD y asociacion en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoService.java`

**Checkpoint**: Base tecnica lista para implementar historias de usuario en paralelo.

---

## Phase 3: User Story 1 - Alta y consulta de departamentos (Priority: P1) MVP

**Goal**: Crear departamentos y consultarlos por clave y por listado paginado.

**Independent Test**: Validar `POST /api/departamentos`, `GET /api/departamentos/{clave}` y `GET /api/departamentos?page=0&size=10`, incluyendo conflicto por clave duplicada.

### Tests for User Story 1 (REQUIRED)

- [X] T020 [P] [US1] Crear pruebas de contrato para alta y consulta de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoContractCreateQueryIntegrationTest.java`
- [X] T021 [P] [US1] Crear pruebas unitarias de alta y paginacion en servicio de departamentos en `src/test/java/com/dsw/practica02/unit/departamentos/DepartamentoServiceCreateQueryTest.java`
- [X] T022 [P] [US1] Crear pruebas de integracion autenticadas para alta y consulta de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoCreateQueryIntegrationTest.java`

### Implementation for User Story 1

- [X] T023 [US1] Implementar create, getByClave y list en DepartamentoServiceImpl en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`
- [X] T024 [US1] Implementar endpoint POST de departamentos en `src/main/java/com/dsw/practica02/departamentos/controller/DepartamentoController.java`
- [X] T025 [US1] Implementar endpoints GET por clave y paginado de departamentos en `src/main/java/com/dsw/practica02/departamentos/controller/DepartamentoController.java`
- [X] T026 [US1] Aplicar validacion de page y size en listado de departamentos en `src/main/java/com/dsw/practica02/departamentos/controller/DepartamentoController.java`
- [X] T027 [US1] Agregar logging de alta y consulta en servicio de departamentos en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`

**Checkpoint**: US1 lista, funcional y demostrable de forma independiente.

---

## Phase 4: User Story 2 - Vincular empleados a departamentos (Priority: P2)

**Goal**: Asociar y reasignar empleados existentes a departamentos, reflejando la relacion en consultas.

**Independent Test**: Crear/actualizar departamento con `empleadosClaves` validas, rechazar claves inexistentes o duplicadas y confirmar resumen de departamento en `GET /api/empleados/{clave}`.

### Tests for User Story 2 (REQUIRED)

- [X] T028 [P] [US2] Crear pruebas de contrato para asociacion y reasignacion con empleadosClaves en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoContractAssignmentIntegrationTest.java`
- [X] T029 [P] [US2] Crear pruebas unitarias de asociacion con empleados inexistentes o duplicados en `src/test/java/com/dsw/practica02/unit/departamentos/DepartamentoServiceAssignmentTest.java`
- [X] T030 [P] [US2] Crear pruebas de integracion de vinculacion departamento-empleado en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoAssignmentIntegrationTest.java`
- [X] T031 [P] [US2] Crear pruebas de seguridad 401/200 para endpoints de asociacion de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoSecurityIntegrationTest.java`

### Implementation for User Story 2

- [X] T032 [US2] Implementar logica transaccional de asociacion y reasignacion en DepartamentoServiceImpl en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`
- [X] T033 [US2] Implementar DTOs de request con empleadosClaves en `src/main/java/com/dsw/practica02/departamentos/dto/DepartamentoCreateRequest.java`
- [X] T034 [US2] Actualizar EmpleadoServiceImpl para devolver resumen de departamento en consulta de empleado en `src/main/java/com/dsw/practica02/empleados/service/EmpleadoServiceImpl.java`
- [X] T035 [US2] Integrar asignacion en POST PUT PATCH de departamentos en `src/main/java/com/dsw/practica02/departamentos/controller/DepartamentoController.java`
- [X] T036 [US2] Agregar logging de asociacion y reasignacion de empleados en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`

**Checkpoint**: US2 lista, funcional y demostrable de forma independiente.

---

## Phase 5: User Story 3 - Mantenimiento y baja de departamentos (Priority: P3)

**Goal**: Actualizar departamentos y bloquear su eliminacion cuando existan empleados asociados.

**Independent Test**: Ejecutar `PUT`, `PATCH` y `DELETE` sobre departamentos existentes/no existentes, validando `409` cuando el departamento tenga empleados.

### Tests for User Story 3 (REQUIRED)

- [X] T037 [P] [US3] Crear pruebas de contrato para PUT PATCH DELETE de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoContractMutationIntegrationTest.java`
- [X] T038 [P] [US3] Crear pruebas unitarias de update y delete restringido en `src/test/java/com/dsw/practica02/unit/departamentos/DepartamentoServiceMutationTest.java`
- [X] T039 [P] [US3] Crear pruebas de integracion para mutaciones y conflicto de borrado en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoMutationIntegrationTest.java`
- [X] T040 [P] [US3] Crear pruebas de seguridad 401/200 para endpoints PUT PATCH DELETE de departamentos en `src/test/java/com/dsw/practica02/integration/departamentos/DepartamentoMutationSecurityIntegrationTest.java`

### Implementation for User Story 3

- [X] T041 [US3] Implementar updatePut y updatePatch en DepartamentoServiceImpl en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`
- [X] T042 [US3] Implementar delete con bloqueo por empleados asociados en DepartamentoServiceImpl en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`
- [X] T043 [US3] Implementar endpoints PUT PATCH DELETE de departamentos en `src/main/java/com/dsw/practica02/departamentos/controller/DepartamentoController.java`
- [X] T044 [US3] Agregar logging de actualizacion y bloqueo de eliminacion en `src/main/java/com/dsw/practica02/departamentos/service/DepartamentoServiceImpl.java`

**Checkpoint**: US3 lista, funcional y demostrable de forma independiente.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cerrar documentacion, cumplimiento constitucional y verificacion final.

- [X] T045 [P] Alinear contrato OpenAPI final con implementacion en `specs/001-crud-departamentos-empleados/contracts/departamentos.openapi.yaml`
- [X] T046 [P] Actualizar quickstart con ejemplos finales de autenticacion y payloads en `specs/001-crud-departamentos-empleados/quickstart.md`
- [X] T047 [P] Actualizar evidencia de cumplimiento constitucional en `specs/001-crud-departamentos-empleados/plan.md`
- [X] T048 Ejecutar suite unitaria de departamentos en `src/test/java/com/dsw/practica02/unit/departamentos/`
- [X] T049 Ejecutar suite de integracion de departamentos y regresion de empleados en `src/test/java/com/dsw/practica02/integration/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: sin dependencias.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todas las historias.
- **Phases 3-5 (User Stories)**: dependen de Phase 2 y pueden ejecutarse en paralelo por capacidad de equipo.
- **Phase 6 (Polish)**: depende de las historias que se quieran liberar.

### User Story Dependencies

- **US1 (P1)**: inicia tras Foundational y entrega MVP funcional.
- **US2 (P2)**: inicia tras Foundational; puede trabajar en paralelo con US1 si hay fixtures.
- **US3 (P3)**: inicia tras Foundational; depende del mismo baseline tecnico.

### Story Completion Order

- Orden recomendado de entrega: `US1 -> US2 -> US3`.
- Orden alternativo con equipo paralelo: `US1`, `US2` y `US3` despues de Foundational, cerrando integracion en Phase 6.

### Within Each User Story

- Tests primero y en rojo antes de implementar.
- Servicio antes de controlador.
- Logging y manejo de errores antes del checkpoint de cierre.

---

## Parallel Execution Examples

### User Story 1

```bash
T020 [US1] pruebas de contrato
T021 [US1] pruebas unitarias
T022 [US1] pruebas de integracion
```

### User Story 2

```bash
T028 [US2] pruebas de contrato
T029 [US2] pruebas unitarias
T030 [US2] pruebas de integracion
T031 [US2] pruebas de seguridad
```

### User Story 3

```bash
T037 [US3] pruebas de contrato
T038 [US3] pruebas unitarias
T039 [US3] pruebas de integracion
T040 [US3] pruebas de seguridad
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Setup.
2. Completar Foundational.
3. Completar US1.
4. Validar US1 de forma independiente y preparar demo.

### Incremental Delivery

1. Entregar US1 (alta y consulta).
2. Entregar US2 (asociacion y reasignacion).
3. Entregar US3 (mantenimiento y baja restringida).
4. Ejecutar Polish para contrato, docs y pruebas completas.

### Parallel Team Strategy

1. Todo el equipo completa Setup + Foundational.
2. Despues, distribucion sugerida:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integracion final y estabilizacion en Phase 6.
