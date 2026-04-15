# Phase 0 Research - CRUD de Departamentos Conectado a Empleados

## Decision 1: Modelo relacional de la vinculacion
- **Decision**: Modelar `Departamento` como nueva entidad y asociar `Empleado` mediante FK de departamento en la tabla de empleados (relacion 1:N).
- **Rationale**: Es el modelo mas simple para cumplir "empleado pertenece como maximo a un departamento" y reutiliza el agregado existente de empleados sin tabla puente.
- **Alternatives considered**:
  - Tabla intermedia departamento_empleado: descartada por complejidad innecesaria para cardinalidad 1:N.
  - Relacion N:M: descartada porque contradice FR-007.

## Decision 2: Regla de borrado de departamentos
- **Decision**: Bloquear `DELETE` de departamento cuando existan empleados asociados y responder conflicto de negocio.
- **Rationale**: Cumple FR-005 y evita dejar empleados sin contexto por borrados accidentales.
- **Alternatives considered**:
  - Borrado en cascada de empleados: descartado por riesgo alto de perdida de datos.
  - Desasignacion automatica en delete: descartada por comportamiento implicito no solicitado.

## Decision 3: Estrategia de asignacion y reasignacion
- **Decision**: Permitir `empleadosClaves` en create/put/patch de departamento para asignar y reasignar en lote de forma transaccional.
- **Rationale**: Reduce llamadas cliente y centraliza reglas de consistencia de asociacion en un solo flujo.
- **Alternatives considered**:
  - Endpoint separado por empleado para reasignar: viable, pero agrega mas pasos para el caso principal de gestion por departamento.
  - Solo asignacion en endpoint dedicado: descartado por no cubrir FR-006 en operaciones CRUD de departamento.

## Decision 4: Forma de respuestas para evitar ciclos
- **Decision**: Responder departamentos con lista de empleados resumidos y responder empleados con resumen de departamento (sin anidamiento recursivo).
- **Rationale**: Evita recursion JSON y payloads innecesarios, manteniendo trazabilidad de la relacion en ambos sentidos.
- **Alternatives considered**:
  - Incluir objetos completos anidados en ambos lados: descartado por riesgo de recursion y sobrecarga.
  - Omitir datos relacionados en respuestas: descartado por incumplir FR-010.

## Decision 5: Validaciones de dominio para departamento
- **Decision**: Mantener convencion de validacion del dominio existente: `clave` 1..20, `nombre` 1..100, `descripcion` 1..100 y no blancos.
- **Rationale**: Mantiene consistencia semantica con empleados y facilita reutilizacion de patrones ya probados.
- **Alternatives considered**:
  - Campos sin limites estrictos: descartado por degradar calidad de datos.
  - Limites distintos por endpoint: descartado por ambiguedad funcional.

## Decision 6: Seguridad y documentacion API
- **Decision**: Aplicar Basic Auth a todo endpoint de departamentos y a contratos OpenAPI/Swagger relacionados.
- **Rationale**: Cumple CA-002 y CA-004 de la constitucion del proyecto.
- **Alternatives considered**:
  - Exponer GET publico: descartado por politica de seguridad por defecto.

## Decision 7: Estrategia de pruebas
- **Decision**: Crear pruebas unitarias para reglas de asociacion (duplicados, inexistentes, delete restringido) y pruebas de integracion para CRUD + vinculacion con PostgreSQL real via Testcontainers.
- **Rationale**: Cubre CA-005 y mitiga regresiones en la integracion entre auth, HTTP y persistencia.
- **Alternatives considered**:
  - Solo pruebas unitarias: descartado por cobertura insuficiente del flujo real.
  - Solo pruebas de integracion: descartado por menor granularidad para diagnostico.

## Resultado de clarificaciones tecnicas

No quedan elementos `NEEDS CLARIFICATION` pendientes para continuar a diseno y contratos.
