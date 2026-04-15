# Tasks: CRUD de Empleados (Backend + Frontend Angular CLI)

**Input**: Design documents from `/specs/001-crud-empleados/`  
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`

**Tests**: Pruebas backend y frontend obligatorias por constitucion (unitarias + integracion + al menos un flujo E2E critico).

**Organization**: Tareas agrupadas por historia de usuario para entrega incremental e independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar el entorno local y dejar Angular CLI operable con start/serve.

- [X] T001 Verificar requisitos de ejecucion y variables de entorno en `specs/001-crud-empleados/quickstart.md`
- [X] T002 [P] Validar servicio PostgreSQL local en `docker/docker-compose.yml`
- [X] T003 [P] Alinear dependencias Node/Angular del monorepo en `frontend/package.json`
- [X] T004 Configurar scripts `start` y `serve` para Angular CLI en `frontend/package.json`
- [X] T005 [P] Crear configuracion de workspace Angular CLI en `frontend/angular.json`
- [X] T006 [P] Crear configuracion TypeScript de app Angular en `frontend/tsconfig.app.json`
- [X] T007 [P] Crear configuracion TypeScript base de Angular en `frontend/tsconfig.json`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establecer bases de backend, contrato y shell frontend antes de historias.

**CRITICAL**: Ninguna historia inicia sin completar esta fase.

- [X] T008 Implementar seguridad Basic Auth para API y Swagger en `src/main/java/com/dsw/practica02/config/SecurityConfig.java`
- [X] T009 [P] Alinear configuracion OpenAPI con `basicAuth` en `src/main/java/com/dsw/practica02/config/OpenApiConfig.java`
- [X] T010 [P] Ajustar entidad Empleado con version de concurrencia en `src/main/java/com/dsw/practica02/empleados/model/Empleado.java`
- [X] T011 [P] Ajustar repositorio de empleados para CRUD y paginacion en `src/main/java/com/dsw/practica02/empleados/repository/EmpleadoRepository.java`
- [X] T012 [P] Ajustar DTOs de requests/responses de empleados en `src/main/java/com/dsw/practica02/empleados/dto/`
- [X] T013 Implementar manejo global de errores de negocio y concurrencia en `src/main/java/com/dsw/practica02/config/GlobalExceptionHandler.java`
- [X] T014 [P] Alinear contrato OpenAPI de empleados con `ETag/If-Match` en `specs/001-crud-empleados/contracts/empleados.openapi.yaml`
- [X] T015 [P] Crear configuracion de dev server Angular CLI en `frontend/angular.json`
- [X] T016 [P] Crear bootstrap de aplicacion Angular para run/start en `frontend/src/main.ts`
- [X] T017 [P] Configurar cliente frontend para autenticacion basica en `frontend/src/app/core/api/empleados-api.service.ts`

**Checkpoint**: Base tecnica lista para ejecutar historias en paralelo.

---

## Phase 3: User Story 1 - Alta de empleado (Priority: P1) MVP

**Goal**: Permitir alta de empleados validos con manejo de duplicados y validaciones.

**Independent Test**: Crear empleado valido y verificar rechazo por clave duplicada o campos invalidos.

### Tests for User Story 1

- [X] T018 [P] [US1] Crear prueba unitaria de alta y duplicados en `src/test/java/com/dsw/practica02/unit/empleados/EmpleadoServiceCreateTest.java`
- [X] T019 [P] [US1] Crear prueba de integracion `POST /api/empleados` en `src/test/java/com/dsw/practica02/integration/empleados/EmpleadoCreateIntegrationTest.java`
- [X] T020 [P] [US1] Crear prueba frontend del formulario de alta en `frontend/src/app/features/empleados/create/empleado-create.component.spec.ts`
- [X] T021 [P] [US1] Crear prueba E2E de alta exitosa y duplicada en `frontend/e2e/empleados-create.e2e-spec.ts`

### Implementation for User Story 1

- [X] T022 [US1] Implementar logica de creacion y validaciones en `src/main/java/com/dsw/practica02/empleados/service/EmpleadoServiceImpl.java`
- [X] T023 [US1] Implementar endpoint `POST /api/empleados` en `src/main/java/com/dsw/practica02/empleados/controller/EmpleadoController.java`
- [X] T024 [P] [US1] Implementar UI de alta de empleados en `frontend/src/app/features/empleados/create/empleado-create.component.ts`
- [X] T025 [US1] Integrar alta con cliente API y mensajes de error en `frontend/src/app/core/api/empleados-api.service.ts`

**Checkpoint**: US1 completa y validable en forma independiente.

---

## Phase 4: User Story 2 - Consulta y listado de empleados (Priority: P2)

**Goal**: Permitir consulta por clave y listado paginado obligatorio.

**Independent Test**: Consultar existente/inexistente y listar con `page` y `size` validos.

### Tests for User Story 2

- [X] T026 [P] [US2] Crear prueba unitaria de consulta y paginacion en `src/test/java/com/dsw/practica02/unit/empleados/EmpleadoServiceCreateQueryTest.java`
- [X] T027 [P] [US2] Crear prueba de integracion `GET` por clave y paginado en `src/test/java/com/dsw/practica02/integration/empleados/EmpleadoQueryIntegrationTest.java`
- [X] T028 [P] [US2] Crear prueba frontend de listado/detalle en `frontend/src/app/features/empleados/list/empleado-list.component.spec.ts`
- [X] T029 [P] [US2] Crear prueba E2E de consulta y paginacion en `frontend/e2e/empleados-query.e2e-spec.ts`

### Implementation for User Story 2

- [X] T030 [US2] Implementar servicio de consulta por clave y lista paginada en `src/main/java/com/dsw/practica02/empleados/service/EmpleadoServiceImpl.java`
- [X] T031 [US2] Implementar endpoints `GET /api/empleados/{clave}` y `GET /api/empleados` en `src/main/java/com/dsw/practica02/empleados/controller/EmpleadoController.java`
- [X] T032 [US2] Implementar validaciones de `page>=0` y `size 1..50` en `src/main/java/com/dsw/practica02/empleados/controller/EmpleadoController.java`
- [X] T033 [P] [US2] Implementar UI de listado y detalle en `frontend/src/app/features/empleados/list/empleado-list.component.ts`

**Checkpoint**: US2 completa y validable en forma independiente.

---

## Phase 5: User Story 3 - Actualizacion y baja con concurrencia (Priority: P3)

**Goal**: Implementar `PUT/PATCH/DELETE` con control optimista `ETag/If-Match`.

**Independent Test**: Verificar `404`, `412` y `428` junto con flujos exitosos de mutacion.

### Tests for User Story 3

- [X] T034 [P] [US3] Crear prueba unitaria de mutaciones y precondiciones en `src/test/java/com/dsw/practica02/unit/empleados/EmpleadoServiceMutationTest.java`
- [X] T035 [P] [US3] Crear prueba de integracion de `PUT/PATCH/DELETE` con `412/428` en `src/test/java/com/dsw/practica02/integration/empleados/EmpleadoMutationIntegrationTest.java`
- [X] T036 [P] [US3] Crear prueba frontend de edicion/baja y conflictos en `frontend/src/app/features/empleados/edit/empleado-edit.component.spec.ts`
- [X] T037 [P] [US3] Crear prueba E2E de conflicto de concurrencia en `frontend/e2e/empleados-concurrency.e2e-spec.ts`

### Implementation for User Story 3

- [X] T038 [US3] Implementar `PUT/PATCH` con validacion de `If-Match` en `src/main/java/com/dsw/practica02/empleados/service/EmpleadoServiceImpl.java`
- [X] T039 [US3] Implementar `DELETE` fisico con manejo `not found` en `src/main/java/com/dsw/practica02/empleados/service/EmpleadoServiceImpl.java`
- [X] T040 [US3] Implementar endpoints `PUT/PATCH/DELETE` con `ETag` y `If-Match` en `src/main/java/com/dsw/practica02/empleados/controller/EmpleadoController.java`
- [X] T041 [P] [US3] Implementar UI de edicion/baja con flujo de refresh en `frontend/src/app/features/empleados/edit/empleado-edit.component.ts`

**Checkpoint**: US3 completa y validable en forma independiente.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cerrar calidad, documentacion y ejecucion completa front/back.

- [X] T042 [P] Actualizar guia de ejecucion front/back y comandos Angular CLI en `specs/001-crud-empleados/quickstart.md`
- [X] T043 [P] Corregir duplicidad de criterio SC-007 en `specs/001-crud-empleados/spec.md`
- [X] T044 [P] Verificar contrato final CRUD y errores `400/404/409/412/428` en `specs/001-crud-empleados/contracts/empleados.openapi.yaml`
- [X] T045 Ejecutar pruebas backend de empleados en `src/test/java/com/dsw/practica02/`
- [X] T046 Ejecutar pruebas frontend unitarias/componentes en `frontend/src/app/features/empleados/`
- [X] T047 Ejecutar smoke de arranque Angular CLI `npm --prefix frontend run start` usando configuracion de `frontend/package.json`

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup (Phase 1): sin dependencias.
- Foundational (Phase 2): depende de Setup y bloquea todas las historias.
- User Stories (Phases 3-5): dependen de Foundational.
- Polish (Phase 6): depende de historias completas.

### User Story Dependencies

- US1 (P1): inicia tras Phase 2; define la base del CRUD.
- US2 (P2): inicia tras Phase 2; usa datos existentes pero es validable sola.
- US3 (P3): inicia tras Phase 2; se valida sola con escenarios de concurrencia.

### Story Completion Order

- Orden recomendado: US1 -> US2 -> US3.
- Orden alternativo con equipo paralelo: US1, US2 y US3 en paralelo tras Phase 2.

### Parallel Opportunities

- En Phase 1: T002, T003, T005, T006 y T007.
- En Phase 2: T009, T010, T011, T012, T014, T015, T016 y T017.
- En cada historia: pruebas backend/frontend/E2E marcadas [P] pueden correrse en paralelo.

---

## Parallel Example: User Story 1

```bash
T018 + T019 + T020 + T021 en paralelo (tests US1)
T022 + T024 en paralelo (backend service y frontend UI en archivos distintos)
```

## Parallel Example: User Story 2

```bash
T026 + T027 + T028 + T029 en paralelo (tests US2)
T030 + T033 en paralelo (backend query y frontend list)
```

## Parallel Example: User Story 3

```bash
T034 + T035 + T036 + T037 en paralelo (tests US3)
T038 + T041 en paralelo (backend mutaciones y frontend edicion)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 (incluyendo Angular CLI start/serve).
2. Completar Phase 2.
3. Completar US1 (Phase 3).
4. Validar US1 de forma independiente antes de continuar.

### Incremental Delivery

1. Entregar US1 (alta).
2. Entregar US2 (consulta/listado).
3. Entregar US3 (actualizacion/baja con concurrencia).
4. Cerrar con Phase 6 (documentacion, ejecucion y smoke final).
