# Quickstart - CRUD de Departamentos Conectado a Empleados

## 1) Prerrequisitos
- Java 17
- Docker y Docker Compose
- Maven (o wrapper `./mvnw`)

## 2) Levantar PostgreSQL con Docker
Variables recomendadas:
- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=dswdb`
- `DB_USER=postgres`
- `DB_PASSWORD=postgres`

Comando:
- `sudo docker compose -f docker/docker-compose.yml up -d`

## 3) Configurar credenciales de autenticacion basica
- `APP_BASIC_USER=admin`
- `APP_BASIC_PASSWORD=admin123`

## 4) Ejecutar la aplicacion
- `mvn spring-boot:run`

## 5) Probar endpoints con autenticacion
Cabecera:
- `Authorization: Basic <base64(admin:admin123)>`

Endpoints de departamentos esperados:
- `POST /api/departamentos`
- `GET /api/departamentos?page=0&size=10`
- `GET /api/departamentos/{clave}`
- `PUT /api/departamentos/{clave}`
- `PATCH /api/departamentos/{clave}`
- `DELETE /api/departamentos/{clave}`

Integracion con empleados:
- `GET /api/empleados/{clave}` debe reflejar el departamento actual del empleado.

Reglas clave:
- `page` obligatorio, minimo `0`
- `size` obligatorio, rango `1..50`
- No se puede eliminar un departamento con empleados asociados.

## 6) Swagger/OpenAPI
- UI: `/swagger-ui.html`
- Docs: `/v3/api-docs`
- Ambas rutas protegidas con Basic Auth.

## 7) Pruebas minimas recomendadas
- Unitarias:
  - validacion de claves y longitudes de departamento
  - rechazo de empleados inexistentes/duplicados al asociar
  - rechazo de delete con empleados asociados
- Integracion:
  - flujo CRUD de departamentos autenticado
  - flujo de asignacion y reasignacion de empleados
  - verificacion de respuesta de empleado con departamento actual
