# Data Model - CRUD de Departamentos Conectado a Empleados

## Entidad: Departamento

### Campos
- `clave` (string, PK, `VARCHAR(20)`, requerido)
- `nombre` (string, `VARCHAR(100)`, requerido)
- `descripcion` (string, `VARCHAR(100)`, requerido)

### Reglas de validacion
- `clave`:
  - obligatorio
  - longitud: 1..20
  - unico en el sistema
- `nombre`, `descripcion`:
  - obligatorios
  - longitud: 1..100
  - no aceptar solo espacios en blanco

### Restricciones de integridad
- PK: `clave`
- Relacion: `Departamento` 1 -> N `Empleado`

## Entidad: Empleado (impacto de relacion)

### Campos relevantes
- `clave` (string, PK, `VARCHAR(20)`)
- `nombre`, `direccion`, `telefono` (existentes)
- `departamento_clave` (FK a `departamentos.clave`, opcional para compatibilidad de datos existentes)

### Reglas de validacion de asociacion
- Cada empleado puede pertenecer a cero o un departamento.
- En operaciones de asociacion, todas las claves de empleados deben existir.
- Una solicitud de asignacion no puede repetir la misma clave de empleado.

## Entidad derivada: Asignacion Departamento-Empleado

Representa la adscripcion vigente de un empleado a un departamento.

### Reglas
- Reasignar un empleado mueve la FK al nuevo departamento.
- No se permite eliminar departamento si tiene empleados asociados.

## Transiciones de estado

### Departamento
- `NO_EXISTE` -> `EXISTE` (CREATE)
- `EXISTE` -> `EXISTE` (PUT/PATCH)
- `EXISTE` -> `NO_EXISTE` (DELETE) solo si no hay empleados asociados

### Asignacion
- `SIN_DEPARTAMENTO` -> `ASIGNADO(deptoA)`
- `ASIGNADO(deptoA)` -> `ASIGNADO(deptoB)` (reasignacion)
- `ASIGNADO(deptoA)` -> `SIN_DEPARTAMENTO` (solo por operacion explicita de desasignacion si se habilita)

## Operaciones y efectos

- `CREATE departamento`: crea departamento y opcionalmente asigna empleados existentes.
- `GET departamento`: retorna datos del departamento y empleados asociados (resumen).
- `LIST departamentos`: paginacion obligatoria `page >= 0`, `size 1..50`.
- `PUT departamento`: reemplaza datos y conjunto de asociaciones.
- `PATCH departamento`: modifica subset de campos y/o asociaciones.
- `DELETE departamento`: bloqueado si hay empleados asociados.
- `GET empleado`: incluye resumen de departamento actual si existe.
