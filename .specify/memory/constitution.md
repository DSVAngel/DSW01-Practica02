<!--
Sync Impact Report
- Version change: 1.0.0 -> 1.1.0
- Modified principles:
	- I. Stack Base Obligatorio -> I. Arquitectura Monorepo Full-Stack Obligatoria
	- II. Seguridad por Defecto con Basic Auth -> II. Seguridad End-to-End con Basic Auth
	- III. Persistencia PostgreSQL con Docker -> III. Contrato API Unico y Sincronizacion Frontend
	- IV. Documentación API con Swagger Protegido -> IV. Persistencia PostgreSQL con Docker
	- V. Calidad, Pruebas y Observabilidad -> V. Calidad y Observabilidad Integral
- Added sections:
	- Politica de Monorepo y Contratos
- Removed sections: Ninguna
- Templates requiring updates:
	- ✅ .specify/templates/plan-template.md
	- ✅ .specify/templates/spec-template.md
	- ✅ .specify/templates/tasks-template.md
	- ⚠ pending: .specify/templates/commands/*.md (ruta no existe en este repositorio)
- Runtime guidance updated:
	- ✅ .github/agents/copilot-instructions.md
- Deferred TODOs: Ninguno
-->
# DSW01-Practica02 Constitution

## Core Principles

### I. Arquitectura Monorepo Full-Stack Obligatoria
El repositorio MUST operar como monorepo para backend y frontend. El backend MUST
mantenerse en Spring Boot 3 + Java 17 y el frontend MUST implementarse en Angular 21 +
TypeScript. Toda nueva funcionalidad MUST declarar en su especificacion el impacto en
backend, frontend o ambos, y MUST mantener limites claros entre aplicaciones.

Rationale: reducir fragmentacion tecnologica y habilitar entrega coordinada de API y UI.

### II. Seguridad End-to-End con Basic Auth
Toda superficie HTTP expuesta por el backend MUST estar protegida por autenticacion
basica en entornos de desarrollo y pruebas, salvo endpoints publicos explicitamente
documentados en la especificacion. El frontend MUST consumir esas rutas autenticadas sin
credenciales hardcodeadas y MUST obtener secretos desde variables de entorno o
configuracion segura.

Rationale: establecer una linea base de seguridad verificable en API y cliente.

### III. Contrato API Unico y Sincronizacion Frontend
OpenAPI MUST ser la fuente de verdad del contrato HTTP. Cualquier cambio de endpoint,
payload o codigos de respuesta MUST actualizar documentacion OpenAPI y adaptar
consumidores Angular en la misma entrega. Los cambios incompatibles MUST declararse en
spec/plan con estrategia de migracion.

Rationale: evitar drift entre backend y frontend y reducir regresiones de integracion.

### IV. Persistencia PostgreSQL con Docker
La persistencia relacional MUST utilizar PostgreSQL. El entorno local MUST poder
levantarse con Docker para asegurar paridad de desarrollo. La configuracion de conexion
MUST externalizar host, puerto, base de datos, usuario y contrasena mediante propiedades
y variables de entorno.

Rationale: portabilidad del entorno y reproducibilidad entre equipos.

### V. Calidad y Observabilidad Integral
Cada historia implementada MUST incluir pruebas backend (unitarias e integracion) y,
cuando afecte UI, pruebas frontend (unitarias/componentes y al menos un flujo E2E
critico). CI MUST ejecutar suites de backend y frontend antes de merge. El sistema MUST
registrar eventos de seguridad, fallos de persistencia y errores de integracion
frontend-backend con trazas diagnosticables.

Rationale: detectar regresiones de forma temprana en todo el monorepo.

## Restricciones Tecnicas y de Seguridad

- Java MUST fijarse en versión 17 en build y CI.
- Angular MUST fijarse en major 21 en `frontend/package.json` y en CI.
- Dependencias de Spring Security, Spring Data JPA y springdoc-openapi MUST declararse
	cuando aplique la funcionalidad backend.
- Secretos y contrasenas MUST inyectarse por variables de entorno o secretos de CI/CD;
	el versionado de secretos reales en repositorio esta prohibido.
- La configuracion MUST declarar parametros explicitos para datasource PostgreSQL,
	autenticacion basica y rutas OpenAPI/Swagger.
- Cualquier desactivacion temporal de seguridad MUST quedar acotada a perfil local y
	documentada con fecha de expiracion.

## Politica de Monorepo y Contratos

- El frontend Angular MUST residir en `frontend/`.
- El backend MUST tener una unica ubicacion oficial (`/` actual o `backend/`) y esa
	decision MUST documentarse en cada `plan.md`.
- Cada feature que modifique contrato HTTP MUST incluir evidencia de sincronizacion de
	consumidores frontend (cliente tipado, servicios o adaptadores).
- El flujo local MUST permitir levantar PostgreSQL, backend y frontend con comandos
	documentados en quickstart.

## Flujo de Desarrollo y Puertas de Calidad

1. Toda especificacion MUST declarar requisitos de monorepo, seguridad, persistencia,
	contrato API y alcance frontend.
2. Todo plan MUST pasar un Constitution Check que valide stack backend/frontend,
	autenticacion, docker y estrategia de pruebas.
3. Toda lista de tareas MUST incluir tareas de contrato OpenAPI, sincronizacion frontend,
	configuracion PostgreSQL en Docker y seguridad basica.
4. Ningun cambio se considera completo sin evidencia de pruebas ejecutadas en backend y,
	cuando aplique, frontend.
5. Toda pull request MUST incluir una sección de cumplimiento constitucional.
6. Las guias de desarrollo (`.specify/templates/*` y guias de agentes) MUST mantenerse
	sincronizadas con esta constitucion.

## Governance

Esta constitucion prevalece sobre guias y practicas de menor jerarquia del repositorio.
Las enmiendas MUST documentar: alcance, impacto, migracion requerida y tipo de cambio de
version.

Política de versionado constitucional (SemVer):
- MAJOR: eliminación o redefinición incompatible de principios o controles obligatorios.
- MINOR: adicion de principios, secciones o controles materialmente nuevos.
- PATCH: aclaraciones, redaccion y mejoras no normativas.

Proceso de enmienda y cumplimiento:
- Toda enmienda MUST aprobarse en revisión técnica del repositorio y actualizar plantillas
	afectadas en `.specify/templates/`.
- Cada plan y PR MUST verificar cumplimiento de principios de forma explicita.
- Se MUST realizar revision de cumplimiento en cada entrega de funcionalidad.

**Version**: 1.1.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-03-11
