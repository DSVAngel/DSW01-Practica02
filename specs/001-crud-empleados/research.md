# Phase 0 Research - CRUD de Empleados

## Decisiones

### 1) Estrategia de persistencia para empleados
- **Decision**: Usar PostgreSQL con tabla `empleados` y PK de negocio `clave` como `VARCHAR(20)`.
- **Rationale**: Se alinea con el requerimiento funcional de clave alfanumérica, evita claves surrogate innecesarias en un CRUD básico y facilita búsquedas directas.
- **Alternatives considered**:
  - `BIGINT` autogenerado: rechazado porque contradice la clave de negocio solicitada.
  - UUID como PK: rechazado por complejidad innecesaria para el alcance.

### 2) Estrategia de actualización
- **Decision**: Exponer ambos métodos: `PUT` (reemplazo completo) y `PATCH` (actualización parcial).
- **Rationale**: Cumple la aclaración del usuario y permite casos operativos distintos sin sobrecargar el dominio.
- **Alternatives considered**:
  - Solo `PUT`: rechazado por menor flexibilidad.
  - Solo `PATCH`: rechazado por no cubrir explícitamente actualización completa.

### 3) Estrategia de eliminación
- **Decision**: Aplicar eliminación física (`DELETE` real) para esta feature.
- **Rationale**: Mantiene MVP simple, consistente con un CRUD básico y sin requisitos de auditoría/recuperación en la especificación.
- **Alternatives considered**:
  - Eliminación lógica: rechazada por agregar estado y filtros adicionales fuera de alcance.
  - Eliminación lógica con restauración: rechazada por complejidad y casos extra no requeridos.

### 4) Seguridad de endpoints
- **Decision**: Requerir Basic Auth para todo endpoint de empleados y documentación OpenAPI/Swagger.
- **Rationale**: Cumple constitución de proyecto y reduce superficie expuesta en entorno base.
- **Alternatives considered**:
  - Endpoints públicos de lectura: rechazados por no estar solicitados y por política de seguridad por defecto.

### 5) Documentación de API
- **Decision**: Definir contrato OpenAPI 3.0 en `contracts/empleados.openapi.yaml`.
- **Rationale**: Permite trazabilidad de contratos antes de implementación y acelera pruebas de contrato.
- **Alternatives considered**:
  - Documentación solo en código: rechazada por menor claridad temprana para planificación.

### 6) Sincronización backend-frontend en monorepo
- **Decision**: Tomar OpenAPI como fuente de verdad para consumo del futuro frontend Angular 21 y versionar cambios de contrato junto con backend.
- **Rationale**: Cumple la constitucion vigente y evita drift entre API y consumidor web.
- **Alternatives considered**:
  - Mantener contrato implícito sin archivo OpenAPI: rechazado por alto riesgo de incompatibilidades.
  - Sincronizar manualmente por documentación textual: rechazado por fragilidad operativa.

### 7) Estrategia de autenticacion en frontend
- **Decision**: Login manual en frontend con credenciales Basic Auth ingresadas por el usuario y almacenadas solo en memoria de sesion.
- **Rationale**: Cumple restricciones de seguridad constitucionales y evita persistencia de secretos en cliente.
- **Alternatives considered**:
  - Credenciales hardcodeadas en entorno: rechazado por riesgo de exposicion de secretos.
  - Persistencia en `localStorage`/`sessionStorage`: rechazada por superficie adicional de fuga.

### 8) Control de concurrencia optimista
- **Decision**: Usar `ETag` en respuestas de recurso y exigir `If-Match` en `PUT`/`PATCH`, respondiendo `412` ante mismatch y `428` cuando falte precondicion.
- **Rationale**: Evita sobrescrituras silenciosas y alinea backend/frontend con semantica HTTP estandar.
- **Alternatives considered**:
  - Campo `version` en payload con `409`: rechazado para mantener acoplamiento bajo y semantica HTTP nativa.
  - Last-write-wins sin control: rechazado por riesgo funcional alto.

### 9) Pruebas objetivo
- **Decision**: Incluir pruebas unitarias (validadores y servicio) y de integración (CRUD completo con seguridad y persistencia).
- **Rationale**: Es requisito constitucional explícito para rutas críticas y calidad base.
- **Alternatives considered**:
  - Solo unitarias: rechazado por no cubrir integración HTTP+DB+auth.
  - Solo integración: rechazado por baja granularidad de diagnóstico.

### 10) Estrategia de listado
- **Decision**: Forzar paginación obligatoria en `GET /api/empleados` con parámetros `page` y `size`, y límite `size <= 50`.
- **Rationale**: Mantiene control de carga en respuestas, mejora estabilidad y refleja la clarificación de negocio.
- **Alternatives considered**:
  - Listado completo sin paginación: rechazado por riesgo de payloads crecientes.
  - `size` máximo 20 o 100: rechazados por menor equilibrio entre usabilidad y rendimiento para este contexto.

## Resultado de clarificaciones técnicas

No quedan `NEEDS CLARIFICATION` en contexto tecnico para pasar a diseno y contratos de Phase 1.
