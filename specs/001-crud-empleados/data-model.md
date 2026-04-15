# Data Model - CRUD de Empleados

## Entidad: Empleado

### Campos
- `clave` (string, PK, `VARCHAR(20)`, requerido)
- `nombre` (string, `VARCHAR(100)`, requerido)
- `direccion` (string, `VARCHAR(100)`, requerido)
- `telefono` (string, `VARCHAR(100)`, requerido)
- `version` (numero entero incremental para control optimista, interno del dominio)

### Reglas de validación
- `clave`:
  - obligatorio
  - longitud: 1..20
  - único en el sistema
- `nombre`, `direccion`, `telefono`:
  - obligatorios
  - longitud: 1..100
  - no aceptar solo espacios en blanco

### Restricciones de integridad
- PK: `clave`
- Unique: implícito por PK
- La `version` se incrementa en cada actualizacion exitosa

### Mapeo de contrato para frontend
- `EmpleadoCreateRequest`: requiere `clave`, `nombre`, `direccion`, `telefono`.
- `EmpleadoPutRequest`: requiere `nombre`, `direccion`, `telefono` (clave inmutable por path).
- `EmpleadoPatchRequest`: requiere al menos una propiedad entre `nombre`, `direccion`, `telefono`.
- `EmpleadoPageResponse`: `page`, `size`, `totalElements`, `totalPages`, `content[]`.
- `ETag` de respuesta representa la `version` vigente del recurso.
- `If-Match` en `PUT`/`PATCH` transporta la version esperada.

## Transiciones de estado

El modelo es sin estado adicional; las transiciones se expresan por existencia:
- `NO_EXISTE` -> `EXISTE` (CREATE)
- `EXISTE` -> `EXISTE` (PUT/PATCH)
- `EXISTE` -> `NO_EXISTE` (DELETE físico)

Estados de concurrencia para mutaciones:
- `PRECONDITION_MISSING` -> respuesta `428 Precondition Required`
- `PRECONDITION_FAILED` -> respuesta `412 Precondition Failed`
- `PRECONDITION_OK` -> actualizacion permitida e incremento de `version`

## Operaciones y efectos
- `CREATE`: inserta nuevo empleado; falla por `clave` duplicada o validación.
- `GET by clave`: retorna empleado o not-found.
- `LIST`: requiere `page` (>=0) y `size` (1..50), retorna página (posible vacía).
- `PUT`: reemplaza completamente `nombre`, `direccion`, `telefono`; falla si no existe.
- `PUT`: requiere `If-Match`; falla por `428` si falta y por `412` si no coincide.
- `PATCH`: modifica subconjunto de campos permitidos; falla si body sin campos validos, si no existe, por `428` si falta `If-Match` y por `412` si no coincide.
- `DELETE`: elimina registro físicamente; falla si no existe.

## Modelo de respuesta paginada

- `page`: número de página actual
- `size`: tamaño de página aplicado
- `totalElements`: total de registros
- `totalPages`: total de páginas
- `content`: lista de empleados de la página solicitada

## Notas de consistencia

- Este modelo es la base para OpenAPI y para consumidores Angular 21 del monorepo.
- Cualquier cambio de campo o validacion debe reflejarse en `contracts/empleados.openapi.yaml`.
