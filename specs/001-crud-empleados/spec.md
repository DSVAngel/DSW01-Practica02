# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-crud-empleados`  
**Created**: 2026-02-25  
**Status**: Draft  
**Input**: User description: "crear un crud de empleados con los campos clave, nombre, direccion y telefono. Donde clave sea el pk, y los demas campos sean de 100 espacios"

## Clarifications

### Session 2026-02-25

- Q: ¿Qué tipo de dato debe tener `clave` como PK? → A: Alfanumérica corta `VARCHAR(20)`.
- Q: ¿Cómo debe comportarse la actualización de empleados? → A: Soportar `PUT` completo y `PATCH` parcial.
- Q: ¿Qué estrategia de eliminación debe usar el CRUD? → A: Eliminación física (`DELETE` definitivo).
- Q: ¿Cómo debe comportarse el listado de empleados? → A: Paginación `page`/`size` obligatoria.
- Q: ¿Cuál debe ser el tamaño máximo de página (`size`)? → A: Máximo 50.

### Session 2026-03-24

- Q: ¿El alcance de la feature incluye frontend Angular o solo backend? → A: Backend + frontend Angular 21 con CRUD completo en esta misma feature.
- Q: ¿Cómo debe autenticarse el frontend contra Basic Auth? → A: Login manual en frontend, credenciales ingresadas por usuario y almacenadas solo en memoria de sesión.
- Q: ¿Cómo se resuelven conflictos de edición concurrente? → A: Control optimista con versión; ante conflicto se devuelve error de concurrencia y se solicita refrescar datos.
- Q: ¿Qué mecanismo API se usa para control optimista? → A: `ETag` + `If-Match`; si no coincide, responder `412 Precondition Failed`.
- Q: ¿Qué código devolver si falta `If-Match`? → A: Responder `428 Precondition Required`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Alta de empleado (Priority: P1)

Como usuario autenticado, quiero registrar un empleado con clave, nombre, dirección y teléfono para iniciar su gestión en el sistema.

**Why this priority**: Sin creación de registros no existe base de datos operativa para el resto del flujo CRUD.

**Independent Test**: Puede validarse de forma independiente creando un empleado nuevo y comprobando que queda disponible para consulta con todos sus campos persistidos.

**Acceptance Scenarios**:

1. **Given** que no existe un empleado con la clave solicitada, **When** el usuario envía datos válidos, **Then** el sistema crea el empleado y confirma el alta.
2. **Given** que ya existe un empleado con la misma clave, **When** el usuario intenta registrarlo, **Then** el sistema rechaza la operación e informa conflicto por clave duplicada.
3. **Given** que algún campo excede el tamaño permitido, **When** el usuario envía la solicitud, **Then** el sistema rechaza la operación con mensaje de validación claro.

---

### User Story 2 - Consulta y listado de empleados (Priority: P2)

Como usuario autenticado, quiero consultar un empleado por clave y listar empleados para localizar información administrativa rápidamente.

**Why this priority**: Una vez hay datos cargados, la consulta es el siguiente valor directo para operación diaria.

**Independent Test**: Puede validarse consultando una clave existente y otra inexistente, además de recuperar una página de resultados con parámetros `page` y `size` válidos.

**Acceptance Scenarios**:

1. **Given** que existe un empleado con una clave válida, **When** el usuario consulta por esa clave, **Then** el sistema devuelve sus datos completos.
2. **Given** que no existe empleado para una clave, **When** el usuario consulta por esa clave, **Then** el sistema responde que no fue encontrado.
3. **Given** que existen empleados registrados, **When** el usuario solicita el listado con `page` y `size` válidos, **Then** el sistema devuelve únicamente la página solicitada.

---

### User Story 3 - Actualización y baja de empleados (Priority: P3)

Como usuario autenticado, quiero actualizar datos de empleados y eliminarlos cuando sea necesario para mantener la base de datos vigente.

**Why this priority**: Completa el ciclo de mantenimiento de información sobre registros ya existentes.

**Independent Test**: Puede validarse actualizando un registro existente y eliminando otro, verificando luego el resultado final en consultas posteriores.

**Acceptance Scenarios**:

1. **Given** que existe un empleado, **When** el usuario actualiza nombre, dirección o teléfono con valores válidos, **Then** el sistema guarda y devuelve la versión actualizada.
2. **Given** que no existe la clave solicitada, **When** el usuario intenta actualizar o eliminar, **Then** el sistema indica que el registro no existe.
3. **Given** que existe un empleado, **When** el usuario solicita su eliminación, **Then** el sistema elimina el registro y deja de retornarlo en consultas.

### Edge Cases

- Intento de crear empleado con `clave` vacía o nula.
- Intento de crear o actualizar `nombre`, `direccion` o `telefono` con más de 100 caracteres.
- Intento de enviar espacios en blanco solamente en campos obligatorios.
- Eliminación repetida de la misma clave ya eliminada.
- Consulta de listado paginado cuando no hay empleados registrados.
- Solicitud `PATCH` sin campos modificables en el cuerpo.
- Solicitud de listado con `page` o `size` inválidos.
- Solicitud de listado con `size` mayor a 50.
- Conflicto por actualización concurrente del mismo empleado desde dos sesiones.
- Solicitud de `PUT`/`PATCH` sin cabecera `If-Match`.

### Constitution Alignment *(mandatory)*

- **CA-001 (Stack)**: La funcionalidad cubre backend Spring Boot 3 + Java 17 y frontend Angular 21, sin introducir tecnologías alternativas.
- **CA-002 (Security)**: Todas las operaciones CRUD requieren autenticación básica; no se definen endpoints públicos para esta feature.
- **CA-003 (Data)**: La feature utiliza almacenamiento relacional existente y conserva compatibilidad con el entorno local ejecutado en contenedores.
- **CA-004 (API Docs)**: Se documentan los endpoints CRUD y sus reglas de validación en la documentación API protegida y se sincroniza su consumo en frontend.
- **CA-005 (Quality)**: Se exigen pruebas unitarias de validación/reglas de negocio y pruebas de integración para backend, más pruebas frontend de componentes y flujo crítico.
- **CA-006 (Monorepo Ops)**: Se definen comandos de ejecución y pruebas para backend y frontend en quickstart.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear empleados con los campos `clave`, `nombre`, `direccion` y `telefono`.
- **FR-002**: El campo `clave` MUST ser único y MUST identificar de forma primaria a cada empleado.
- **FR-002a**: El campo `clave` MUST almacenarse como texto alfanumérico con longitud máxima de 20 caracteres.
- **FR-003**: Los campos `nombre`, `direccion` y `telefono` MUST aceptar como máximo 100 caracteres cada uno.
- **FR-004**: El sistema MUST rechazar operaciones de creación o actualización que incumplan reglas de longitud o campos obligatorios.
- **FR-005**: El sistema MUST permitir consultar un empleado por `clave`.
- **FR-006**: El sistema MUST permitir listar empleados con paginación obligatoria usando parámetros `page` y `size`.
- **FR-006a**: El parámetro `size` MUST aceptar valores de 1 a 50; valores fuera de rango MUST ser rechazados.
- **FR-007**: El sistema MUST permitir actualización completa mediante `PUT` para `nombre`, `direccion` y `telefono` de un empleado existente identificado por `clave`.
- **FR-007a**: El sistema MUST permitir actualización parcial mediante `PATCH` para uno o más campos entre `nombre`, `direccion` y `telefono`.
- **FR-008**: El sistema MUST permitir eliminar físicamente un empleado existente identificado por `clave`.
- **FR-009**: El sistema MUST devolver respuesta explícita de no encontrado para consulta, actualización o eliminación de claves inexistentes.
- **FR-010**: El sistema MUST requerir autenticación para todas las operaciones CRUD de empleados.
- **FR-011**: El sistema MUST incluir un frontend Angular 21 con pantallas para crear, consultar/listar, actualizar y eliminar empleados.
- **FR-012**: El frontend MUST consumir exclusivamente el contrato OpenAPI vigente de empleados y mantenerse sincronizado con cambios de endpoint/payload.
- **FR-013**: El frontend MUST mostrar mensajes de error de validación, conflicto y no encontrado de forma comprensible para el usuario.
- **FR-014**: El frontend MUST solicitar credenciales al usuario para autenticación básica y enviarlas en cada llamada autenticada.
- **FR-015**: El frontend MUST almacenar credenciales solo en memoria de sesión (sin persistir en localStorage, sessionStorage ni archivos de configuración).
- **FR-016**: El sistema MUST aplicar control optimista de concurrencia para actualizaciones (`PUT`/`PATCH`) de empleados.
- **FR-017**: Ante conflicto de concurrencia, el sistema MUST responder con error explícito y el frontend MUST pedir refrescar datos antes de reintentar.
- **FR-018**: Las operaciones `PUT` y `PATCH` MUST requerir cabecera `If-Match` con el `ETag` de la versión vigente del empleado.
- **FR-019**: Si `If-Match` no coincide con la versión actual, el sistema MUST responder `412 Precondition Failed`.
- **FR-020**: Si falta `If-Match` en `PUT`/`PATCH`, el sistema MUST responder `428 Precondition Required`.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa un registro administrativo con `clave` (`VARCHAR(20)`) como identificador único y `nombre`, `direccion`, `telefono` como atributos textuales de hasta 100 caracteres.

### Assumptions & Dependencies

- Se asume que `clave` es provista por el usuario y no se autogenera.
- Se asume que todos los campos (`clave`, `nombre`, `direccion`, `telefono`) son obligatorios para creación.
- Se asume que la gestión de autenticación y persistencia base ya está definida por lineamientos del proyecto.
- Esta feature depende de disponibilidad de entorno de base de datos y del acceso autenticado al servicio.
- Se asume que el frontend Angular 21 se implementa en `frontend/` dentro del monorepo.
- Se asume que el login frontend es de sesión temporal y requiere reingreso de credenciales al recargar la aplicación.
- Se asume que cada operación de actualización incluye referencia de versión del empleado para validación optimista.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de altas válidas de empleados persiste correctamente en el primer intento durante pruebas funcionales.
- **SC-002**: El 100% de solicitudes con campos de más de 100 caracteres es rechazado con mensaje de validación comprensible.
- **SC-003**: Al menos 95% de operaciones CRUD en pruebas de integración completa en menos de 2 segundos por solicitud.
- **SC-004**: El 100% de operaciones sobre claves inexistentes responde con resultado de no encontrado y sin efectos secundarios.
- **SC-005**: El flujo crítico frontend (alta, consulta/listado, edición y baja) completa exitosamente en al menos 95% de ejecuciones E2E.
- **SC-006**: El 100% de conflictos de concurrencia simulados en pruebas de integración/E2E se detecta y se comunica al usuario sin sobrescritura silenciosa.
- **SC-007**: El 100% de actualizaciones `PUT`/`PATCH` sin `If-Match` es rechazado con `428`, y con `If-Match` desactualizado es rechazado con `412`.
