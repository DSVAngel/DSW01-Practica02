# DSW01-Practica02 Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-03-11

## Active Technologies
- PostgreSQL (ejecución local con Docker) (001-crud-empleados)
- PostgreSQL (tablas `departamentos` y `empleados` con relacion por FK, entorno local Docker) (001-crud-departamentos-empleados)
- Java 17 (backend), TypeScript + Angular 21 (consumidor frontend del contrato) + Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi, OpenAPI 3.0.3 (001-crud-departamentos-empleados)
- PostgreSQL (tabla `empleados`) (001-crud-departamentos-empleados)
- Java 17 (backend), TypeScript + Angular 21 (frontend) + Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi, Angular 21 (001-crud-departamentos-empleados)

- Java 17 + Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi (001-crud-empleados)
- Angular 21 + TypeScript (frontend monorepo)

## Project Structure

```text
src/
test/
frontend/
docker/
specs/
```

## Commands

- `mvn test`
- `mvn spring-boot:run`
- `npm --prefix frontend install`
- `npm --prefix frontend run test`
- `npm --prefix frontend run build`

## Code Style

Java 17: Follow standard conventions
TypeScript/Angular 21: Follow Angular style guide and strict typing

## Recent Changes
- 001-crud-departamentos-empleados: Added Java 17 (backend), TypeScript + Angular 21 (frontend) + Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi, Angular 21
- 001-crud-departamentos-empleados: Added Java 17 (backend), TypeScript + Angular 21 (frontend) + Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi, Angular 21
- 001-crud-departamentos-empleados: Added Java 17 (backend), TypeScript + Angular 21 (consumidor frontend del contrato) + Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi, OpenAPI 3.0.3


<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
