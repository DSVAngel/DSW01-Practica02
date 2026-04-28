# Instrucciones de Configuración - Docker + Aiven

## 1️⃣ Desarrollo Local (docker-compose con BD local)

```bash
# Copia el archivo .env.example
cp .env.example .env

# Levanta los servicios (BD local + backend + frontend)
cd docker
docker-compose up -d
```

**Variables usadas:**
- `DB_NAME=dswdb`
- `DB_USER=postgres`
- `DB_PASSWORD=postgres`
- Base de datos: PostgreSQL local en contenedor

---

## 2️⃣ Desarrollo con Aiven (docker-compose)

Configura tu `.env` con credenciales de Aiven:

```bash
# .env
DB_URL=jdbc:postgresql://pg-264ab7d8-danielangel-d392.b.aivencloud.com:28441/defaultdb?sslmode=require
DB_USERNAME=avnadmin
DB_PASSWORD=
```

```bash
cd docker
docker-compose up -d
```

---

## 3️⃣ Producción - Docker Hub Workflow (CI/CD)

El workflow `.github/workflows/docker-hub.yml` construye y sube imágenes Docker a Docker Hub **con credenciales de Aiven bakeadas en la imagen**.

**Secretos necesarios en GitHub:**

```
DOCKER_USERNAME         = tu_usuario_docker
DOCKER_PASSWORD         = tu_token_docker
AIVEN_DB_URL            = jdbc:postgresql://pg-xxx.b.aivencloud.com:xxxxx/defaultdb?sslmode=require
AIVEN_DB_USERNAME       = avnadmin
AIVEN_DB_PASSWORD       = tu_contraseña_aiven
```

**Configurar secretos en GitHub:**
```
GitHub > Settings > Secrets and variables > Actions > New repository secret
```

**¿Cómo funciona?**
- El workflow pasa `AIVEN_DB_URL`, `AIVEN_DB_USERNAME`, `AIVEN_DB_PASSWORD` como `--build-arg` a Docker Build
- El `Dockerfile` usa `ARG` para recibir estos valores y los convierte en `ENV`
- La imagen Docker se construye con la configuración de Aiven y se sube a Docker Hub
- Cuando se ejecute el contenedor, tendrá preconfigurada la conexión a Aiven

---

## 4️⃣ Ejecutar Docker localmente con Aiven

**Opción A: Construcción local con variables**
```bash
# Construir imagen con credenciales de Aiven
docker build \
  --build-arg DB_URL="jdbc:postgresql://pg-xxx.b.aivencloud.com:28441/defaultdb?sslmode=require" \
  --build-arg DB_USERNAME="avnadmin" \
  --build-arg DB_PASSWORD="AVNS_xxx" \
  -t dsw-backend:latest .

# Ejecutar
docker run -d -p 8080:8080 dsw-backend:latest
```

**Opción B: Usar imagen de Docker Hub (ya con Aiven preconfigurado)**
```bash
docker run -d \
  -p 8080:8080 \
  tu_usuario/dsw-backend:latest
```

---

## 5️⃣ Variables de Entorno Disponibles

| Variable | Defecto | Descripción |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/dswdb?sslmode=disable` | URL de conexión a BD |
| `DB_USERNAME` | `postgres` | Usuario de BD |
| `DB_PASSWORD` | `postgres` | Contraseña de BD |
| `SERVER_PORT` | `8080` | Puerto del servidor |
| `APP_BASIC_USER` | `admin` | Usuario básico de autenticación |
| `APP_BASIC_PASSWORD` | `admin123` | Contraseña básica |

---

## 🔐 Flujo de Seguridad

### Desarrollo Local
- Las credenciales están en `.env` (NO versionado en Git)
- Se cargan en `docker-compose` como variables de entorno
- Base de datos local o Aiven según configuración

### Producción (GitHub Actions → Docker Hub)
1. Defines secretos en GitHub (Settings > Secrets)
2. El workflow los usa como `--build-arg`
3. Docker Build los integra en la imagen como ENV
4. La imagen se sube a Docker Hub con configuración de Aiven
5. Otros servidores descargan la imagen y la ejecutan

### ⚠️ Consideraciones de Seguridad
- **Nunca** guardes credenciales de Aiven en el repositorio
- El archivo `.env` debe estar en `.gitignore` (ya está)
- Los secretos de GitHub están encriptados en tránsito y en reposo
- La imagen Docker en Docker Hub **contiene las credenciales** (considera privada)
- Para máxima seguridad, pasa credenciales en **tiempo de ejecución** (sin bakelarlas):

```bash
# Pasar credenciales sin bakelarlas en la imagen:
docker run -d \
  -e DB_URL="jdbc:postgresql://pg-xxx.b.aivencloud.com:28441/defaultdb?sslmode=require" \
  -e DB_USERNAME="avnadmin" \
  -e DB_PASSWORD="AVNS_xxx" \
  -p 8080:8080 \
  tu_usuario/dsw-backend:latest
```

---

## 📋 Checklist de Configuración

- [ ] Crear secretos en GitHub (DOCKER_USERNAME, DOCKER_PASSWORD, AIVEN_DB_URL, etc.)
- [ ] Copiar `.env.example` a `.env` para desarrollo local
- [ ] Configurar credenciales de Aiven en `.env` si vas a usar docker-compose con Aiven
- [ ] Probar `docker-compose up` en desarrollo
- [ ] Hacer push a `master` para que el workflow construya y suba a Docker Hub
- [ ] Verificar que la imagen aparece en Docker Hub
- [ ] Descargar y ejecutar la imagen: `docker run tu_usuario/dsw-backend:latest`

