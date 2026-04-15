# Feature Specification: CRUD de Departamentos Conectado a Empleados

**Feature Branch**: `001-crud-departamentos-empleados`  
**Created**: 2026-03-08  
**Status**: Draft  
**Input**: User description: "ahora vamos a agregar un crud de departamentos pero conectado a empleados"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Alta y consulta de departamentos (Priority: P1)

Como usuario autenticado, quiero crear y consultar departamentos para organizar a los empleados por unidad de trabajo.

**Why this priority**: Sin departamentos disponibles no existe estructura organizativa para vincular empleados.

**Independent Test**: Puede validarse creando un departamento con datos válidos, consultandolo por su clave y verificando que aparece en listados.

**Acceptance Scenarios**:

1. **Given** que no existe un departamento con la clave solicitada, **When** el usuario registra un departamento valido, **Then** el sistema lo crea y confirma el alta.
2. **Given** que existe un departamento con la clave consultada, **When** el usuario solicita su detalle, **Then** el sistema devuelve su informacion y sus empleados asociados.
3. **Given** que ya existe un departamento con la misma clave, **When** el usuario intenta crearlo de nuevo, **Then** el sistema rechaza la operacion por duplicidad.

---

### User Story 2 - Vincular empleados a departamentos (Priority: P2)

Como usuario autenticado, quiero asignar y reasignar empleados a departamentos para mantener su adscripcion organizativa actualizada.

**Why this priority**: La relacion con empleados entrega el valor principal pedido para esta funcionalidad.

**Independent Test**: Puede validarse asignando empleados existentes a un departamento y verificando que la relacion se refleja en consulta de empleado y departamento.

**Acceptance Scenarios**:

1. **Given** que existen empleados y un departamento activo, **When** el usuario asigna empleados al departamento, **Then** el sistema guarda la relacion y la muestra en consultas posteriores.
2. **Given** que un empleado ya pertenece a otro departamento, **When** el usuario lo reasigna, **Then** el sistema actualiza la adscripcion al nuevo departamento.
3. **Given** que se intenta asociar una clave de empleado inexistente, **When** el usuario envia la solicitud, **Then** el sistema rechaza la operacion con mensaje de validacion.

---

### User Story 3 - Mantenimiento y baja de departamentos (Priority: P3)

Como usuario autenticado, quiero actualizar y eliminar departamentos para mantener la estructura organizativa vigente.

**Why this priority**: Completa el ciclo de mantenimiento del catalogo de departamentos una vez creada y usada la relacion.

**Independent Test**: Puede validarse actualizando nombre o descripcion de un departamento y luego intentando su eliminacion con y sin empleados asociados.

**Acceptance Scenarios**:

1. **Given** que existe un departamento, **When** el usuario actualiza sus datos con valores validos, **Then** el sistema guarda los cambios y devuelve la version actualizada.
2. **Given** que el departamento tiene empleados asociados, **When** el usuario intenta eliminarlo, **Then** el sistema rechaza la eliminacion e indica que primero debe dejarlo sin empleados.
3. **Given** que el departamento no tiene empleados asociados, **When** el usuario solicita su eliminacion, **Then** el sistema lo elimina y deja de retornarlo en consultas.

---

### Edge Cases

- Intento de crear un departamento con clave o nombre vacio.
- Intento de crear o actualizar un departamento con nombre o descripcion por encima del limite permitido.
- Asociacion de un mismo empleado mas de una vez al mismo departamento en la misma solicitud.
- Intento de asociar empleados inexistentes durante creacion o actualizacion de departamento.
- Intento de eliminar un departamento inexistente.
- Consulta paginada cuando no existen departamentos registrados.
- Reasignacion masiva de empleados a un departamento con parte de claves invalidas.

### Constitution Alignment *(mandatory)*

- **CA-001 (Stack)**: La funcionalidad se mantiene dentro del stack constitucional Spring Boot 3 + Java 17.
- **CA-002 (Security)**: Todas las operaciones de departamentos y su relacion con empleados requieren autenticacion basica; no se agregan endpoints publicos.
- **CA-003 (Data)**: La feature agrega persistencia para departamentos y su vinculo con empleados, manteniendo PostgreSQL y entorno local Docker como base de ejecucion.
- **CA-004 (API Docs)**: Se actualiza la documentacion API para incluir operaciones CRUD de departamentos y reglas de asociacion con empleados en rutas protegidas.
- **CA-005 (Quality)**: Se requieren pruebas unitarias de reglas de asociacion y pruebas de integracion para flujos CRUD y de relacion departamento-empleado.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear departamentos con `clave`, `nombre` y `descripcion`.
- **FR-002**: El campo `clave` de departamento MUST ser unico y obligatorio.
- **FR-003**: El sistema MUST permitir consultar un departamento por `clave` y listar departamentos con paginacion obligatoria mediante `page` y `size`.
- **FR-004**: El sistema MUST permitir actualizacion completa y parcial de datos de un departamento existente.
- **FR-005**: El sistema MUST permitir eliminar un departamento solo cuando no tenga empleados asociados.
- **FR-006**: El sistema MUST permitir asociar uno o mas empleados existentes a un departamento en operaciones de creacion y actualizacion.
- **FR-007**: Cada empleado MUST estar asociado como maximo a un departamento a la vez.
- **FR-008**: El sistema MUST permitir reasignar un empleado de un departamento a otro.
- **FR-009**: El sistema MUST rechazar asociaciones con empleados inexistentes o duplicados dentro de la misma solicitud.
- **FR-010**: El sistema MUST incluir en la consulta de departamento el conjunto de empleados asociados y en la consulta de empleado su departamento actual.
- **FR-011**: El sistema MUST rechazar operaciones sobre departamentos inexistentes con respuesta explicita de no encontrado.
- **FR-012**: El sistema MUST aplicar autenticacion basica a todas las operaciones CRUD de departamentos y de asociacion con empleados.

### Key Entities *(include if feature involves data)*

- **Departamento**: Unidad organizativa identificada por una clave unica, con nombre y descripcion administrables.
- **Empleado**: Persona registrada en el sistema que puede estar adscrita a un unico departamento.
- **Asignacion Departamento-Empleado**: Relacion de pertenencia que vincula cada empleado con un departamento y permite su reasignacion.

### Assumptions & Dependencies

- Se asume que el catalogo de empleados ya existe y esta operativo en esta base de codigo.
- Se asume que `clave`, `nombre` y `descripcion` de departamento siguen reglas de validacion consistentes con el dominio existente de empleados.
- Se asume que eliminar un departamento con empleados asociados debe ser bloqueado para evitar perdida de contexto organizativo.
- Esta feature depende de disponibilidad de PostgreSQL y credenciales de autenticacion basicas configuradas.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de departamentos creados con datos validos queda disponible para consulta en el primer intento.
- **SC-002**: El 100% de intentos de asignacion con empleados inexistentes es rechazado con un mensaje de validacion claro.
- **SC-003**: Al menos 95% de operaciones CRUD y de asignacion departamento-empleado finaliza en menos de 2 segundos durante pruebas de integracion.
- **SC-004**: El 100% de intentos de eliminar departamentos con empleados asociados es bloqueado sin alterar asociaciones existentes.
