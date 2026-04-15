# Quickstart - CRUD de Empleados

## 1) Prerrequisitos
- Java 17
- Docker y Docker Compose
- Maven (o wrapper `./mvnw`)
- Node.js LTS y npm (para consumidor Angular 21 en monorepo)

## 2) Levantar PostgreSQL con Docker
Ejemplo de variables:
- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=dswdb`
- `DB_USER=postgres`
- `DB_PASSWORD=postgres`

Si existe `docker/docker-compose.yml`:
- `docker compose -f docker/docker-compose.yml up -d`

## 3) Configurar credenciales de autenticación básica
- `APP_BASIC_USER=admin`
- `APP_BASIC_PASSWORD=admin123`

## 4) Ejecutar la aplicación
- Con wrapper (si existe): `./mvnw spring-boot:run`
- Con Maven instalado: `mvn spring-boot:run`

## 4.1) Frontend Angular (si existe carpeta `frontend/`)
- Instalar dependencias: `npm --prefix frontend install`
- Levantar frontend (dev server): `npm --prefix frontend run start`
- Alias de arranque: `npm --prefix frontend run serve`
- Ejecutar pruebas: `npm --prefix frontend run test`
- Build: `npm --prefix frontend run build`

## 4.2) Login frontend (Basic Auth)
- El usuario ingresa `username/password` en pantalla de login.
- Las credenciales se mantienen solo en memoria de sesion (no persistir en storage).

## 5) Probar endpoints con autenticación
Cabecera:
- `Authorization: Basic <base64(admin:admin123)>`

Endpoints esperados:
- `POST /api/empleados`
- `GET /api/empleados?page=0&size=10`
- `GET /api/empleados/{clave}`
- `PUT /api/empleados/{clave}`
- `PATCH /api/empleados/{clave}`
- `DELETE /api/empleados/{clave}`

Reglas de paginación:
- `page` obligatorio, mínimo `0`
- `size` obligatorio, rango `1..50`

Reglas de concurrencia:
- `PUT`/`PATCH` requieren cabecera `If-Match`.
- Si falta `If-Match` -> `428 Precondition Required`.
- Si `If-Match` no coincide -> `412 Precondition Failed`.

## 6) Swagger/OpenAPI
- UI: `/swagger-ui.html`
- Docs: `/v3/api-docs`
- Ambas rutas protegidas con Basic Auth.

## 6.1) Contrato para consumo frontend
- Contrato fuente: `specs/001-crud-empleados/contracts/empleados.openapi.yaml`
- Cualquier cambio en endpoint/payload debe actualizar este contrato antes de integrar UI.

## 7) Pruebas mínimas recomendadas
- Unitarias:
  - validación de longitudes y campos obligatorios
  - lógica de servicio para no encontrado/duplicado
- Integración:
  - flujo CRUD completo autenticado
  - validación de paginación (`page`/`size`) y límites de `size`
  - validación de respuestas para errores de negocio

## 8) Verificación rápida de contrato
- Revisar que OpenAPI incluye `POST/GET/PUT/PATCH/DELETE` de `/api/empleados`.
- Confirmar respuestas `400/404/409/412/428` y seguridad `basicAuth` en paths de empleados.

## 9) Verificacion rapida de concurrencia
- `GET /api/empleados/{clave}` y capturar `ETag`.
- Ejecutar `PUT` con `If-Match` correcto y confirmar exito.
- Repetir `PUT` con `If-Match` viejo y confirmar `412`.
- Ejecutar `PATCH` sin `If-Match` y confirmar `428`.
