# Emprendia - Plataforma de Emprendimientos

Recordar no crear más archivos siempre actualizar los 3 .md existentes
y en cada micro solo actualizar el README.md con la descripción general y arquitectura específica de cada microservicio, sin entrar en detalles técnicos (eso va en DEVELOPER_GUIDE.md).

## 📚 Documentación del Proyecto

### Documentos Principales (léelos en orden)

1. **README.md** (este archivo) - Introducción y arquitectura general
2. **[DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)** - Guía técnica completa
   - Compilación y ejecución
   - Comunicación entre microservicios (Feign + Circuit Breaker)
   - Sistema de imágenes
   - Configuración por ambiente
   - Troubleshooting
3. **[API_GUIDE.md](API_GUIDE.md)** - Endpoints y Postman
   - Colecciones Postman
   - Todos los endpoints documentados
   - Ejemplos de uso completos
   - Testing automatizado

### Documentación Adicional

- **[postman/README.md](postman/README.md)** - Guía de colecciones Postman
- **[../db/tesis_db.md](../db/tesis_db.md)** - Modelo de base de datos completo

---

## 🎯 Quick Start

```powershell
# 1. Compilar
./gradlew clean build -x test

# 2. Ejecutar servicios (en orden)
cd shared-service && ./gradlew bootRun --args='--spring.profiles.active=local'
cd user-service && ./gradlew bootRun --args='--spring.profiles.active=local'
cd entrepreneurship-service && ./gradlew bootRun --args='--spring.profiles.active=local'
cd event-service && ./gradlew bootRun --args='--spring.profiles.active=local'

# 3. Verificar
curl http://localhost:8084/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# 4. Probar con Postman
# Importar: postman/*.json
# Ejecutar: 00-Auth > Login
# Usar cualquier colección
```

---

## Descripción General

Emprendia es una plataforma de **microservicios independientes** construida con **Spring Boot 3.5.7** para gestionar emprendimientos, eventos y la participación de negocios en ferias y exposiciones.


---

## Arquitectura

```
micros/                              ← Multi-project root (build/deploy conveniente)
├── settings.gradle                  ← Une los 4 proyectos para build conjunto
├── build.gradle                     ← Solo declara group/version, sin subprojects{}
│
├── shared-service/                  → Puerto 8084 | Catálogos compartidos + Sistema de Gestión de Imágenes
│   ├── settings.gradle              ← Standalone: rootProject.name = 'shared-service'
│   └── build.gradle                 ← Standalone: todas las dependencias declaradas (incluye AWS S3 SDK)
│
├── user-service/                    → Puerto 8081 | Usuarios y autenticación
│   ├── settings.gradle
│   └── build.gradle
│
├── entrepreneurship-service/        → Puerto 8082 | Emprendimientos
│   ├── settings.gradle
│   └── build.gradle
│
└── event-service/                   → Puerto 8083 | Eventos e invitaciones
    ├── settings.gradle
    └── build.gradle
```

### Despliegue Independiente vs Conjunto

```bash
# Despliegue independiente (producción real)
cd user-service
gradle bootRun --args='--spring.profiles.active=prod'

# Build conjunto desde root (CI/CD o desarrollo local completo)
./gradlew build
./gradlew :user-service:bootRun --args='--spring.profiles.active=local'
```

### Sistema de Gestión de Imágenes

**shared-service** incluye un sistema completo de gestión de imágenes con almacenamiento local (desarrollo) y cloud (producción).

#### Arquitectura de Storage

```
Strategy Pattern
     │
     ▼
StorageService (interface)
     │
     ├──→ LocalStorageService      (@ConditionalOnProperty: storage.type=local)
     │    └── ./uploads/
     │
     └──→ CloudStorageService      (@ConditionalOnProperty: storage.type=cloud)
          └── Digital Ocean Spaces (S3-compatible)
```

#### Límites por Entidad

| Entidad | Límite de Imágenes |
|---|---|
| **USER** | 5 imágenes |
| **ENTREPRENEURSHIP** | 10 imágenes |
| **EVENT** | 20 imágenes |

#### Validaciones

- **Max file size**: 5 MB
- **Formatos permitidos**: JPEG, PNG, WebP, GIF
- **Dimensiones**: Extraídas automáticamente de metadata
- **Nombre único**: `{timestamp}_{uuid}.{extension}`

#### Estructura de Rutas

```
users/{userId}/profile.jpg
users/{userId}/gallery/{timestamp}_{uuid}.jpg
entrepreneurships/{id}/logo.jpg
entrepreneurships/{id}/gallery/{timestamp}_{uuid}.jpg
events/{id}/cover.jpg
events/{id}/gallery/{timestamp}_{uuid}.jpg
```

#### Configuración por Ambiente

**Local (Development):**
```yaml
storage:
  type: local
  path: ./uploads
```
Archivos servidos por: `GET /api/files/**` (FileServeController)

**Production (Digital Ocean Spaces):**
```yaml
storage:
  type: cloud
  do-spaces:
    endpoint: https://nyc3.digitaloceanspaces.com
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}
```
URLs públicas directas (NO pasan por la API).

#### Tabla de Base de Datos

`image_gallery` — Almacena metadata de imágenes con diseño polimórfico:

```sql
CREATE TABLE image_gallery (
  image_id SERIAL PRIMARY KEY,
  entity_type VARCHAR(50) NOT NULL,  -- 'USER' | 'ENTREPRENEURSHIP' | 'EVENT'
  entity_id BIGINT NOT NULL,
  image_url TEXT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  display_order INT,[README.md](README.md)
  alt_text VARCHAR(255),
  description TEXT,
  width_px INT,
  height_px INT,
  file_size_kb INT,
  mime_type VARCHAR(50),
  uploaded_by_user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Spring Boot | 3.4.1 | Framework base |
| Spring Cloud | 2024.0.0 | OpenFeign + Circuit Breaker |
| Spring Web | - | REST API |
| Spring Data JPA | - | Persistencia |
| Spring Security + OAuth2 Resource Server | - | Autenticación JWT |
| Keycloak | - | Identity Provider (realm: `emprendia`) |
| QueryDSL | 5.1.0 | Consultas tipadas (`BooleanBuilder`, `JPAQueryFactory`) |
| MapStruct | 1.6.3 | Mapeo de objetos (`@Mapper`, `@BeanMapping`) |
| Lombok | - | Reducción de boilerplate |
| Apache Commons Text | 1.12.0 | Utilidades de texto (`CaseUtils`) |
| AWS S3 SDK | 2.28.29 | Storage en cloud (Digital Ocean Spaces) |
| PostgreSQL | - | Base de datos (`emprendia_db`) |
| Gradle | 8.8 | Build tool |
| Java | 21 | Runtime |

---

## Estructura de Cada Microservicio

```
{service}/
├── settings.gradle                              ← Standalone
├── build.gradle                                 ← Standalone con todas las dependencias
└── src/
    ├── main/
    │   ├── java/com/project/emprendia/{domain}/
    │   │   ├── configuration/
    │   │   │   ├── QueryDslConfig.java           ← Bean JPAQueryFactory
    │   │   │   ├── SecurityConfig.java           ← JWT Resource Server + 401/403 handlers
    │   │   │   └── KeycloakJwtAuthenticationConverter.java ← Extrae roles de realm_access
    │   │   ├── controller/                       ← REST endpoints
    │   │   ├── domain/                           ← Entidades JPA (@Entity)
    │   │   ├── dto/                              ← *Request.java y *Response.java
    │   │   ├── enums/                            ← Enumeraciones del dominio
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java   ← @RestControllerAdvice
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── DuplicateResourceException.java
    │   │   ├── mapping/mapper/                   ← Interfaces MapStruct
    │   │   ├── repository/
    │   │   │   ├── *Repository.java              ← JpaRepository
    │   │   │   └── *QueryRepository.java         ← QueryDSL con BooleanBuilder
    │   │   ├── service/
    │   │   │   ├── *Service.java                 ← Interface
    │   │   │   └── impl/*ServiceImpl.java        ← Implementación
    │   │   └── util/
    │   │       ├── DateUtil.java                 ← Formateo de fechas + CaseUtils
    │   │       ├── SecurityUtil.java             ← Extrae keycloakId/username del JWT actual
    │   │       └── SecurityConstants.java        ← Constantes de roles y claims JWT
    │   └── resources/
    │       └── application.yml                   ← Configuración multi-perfil (local/dev/prod)
    └── test/
        └── java/com/project/emprendia/{domain}/
            └── service/impl/                     ← Tests unitarios con Mockito (@ExtendWith)
```

---

## Shared Service – Sistema de Imágenes

El **shared-service** incluye un sistema completo de gestión de imágenes polimórfico que soporta tres tipos de entidades: `USER`, `ENTREPRENEURSHIP` y `EVENT`.

### Componentes Principales

```
shared-service/src/main/java/com/project/emprendia/shared/
├── controller/
│   ├── ImageGalleryController.java              ← API de gestión de imágenes
│   └── FileServeController.java                 ← Servir archivos en storage local
├── domain/
│   ├── ImageGallery.java                        ← Entidad polimórfica con entity_type
│   └── enums/EntityType.java                    ← USER | ENTREPRENEURSHIP | EVENT
├── service/
│   ├── ImageGalleryService.java
│   └── impl/ImageGalleryServiceImpl.java        ← Validaciones (tamaño, formato, límites)
├── storage/
│   ├── StorageService.java                      ← Interface abstracta
│   └── impl/
│       ├── LocalStorageService.java             ← Almacenamiento en ./uploads/
│       └── CloudStorageService.java             ← Digital Ocean Spaces (S3-compatible)
└── dto/
    ├── ImageGalleryRequest.java
    ├── ImageGalleryResponse.java
    ├── ImageUploadRequest.java
    └── ImageUploadResponse.java
```

### Strategy Pattern para Storage

```java
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
@Service
public class LocalStorageService implements StorageService {
    // Implementación para desarrollo local
}

@ConditionalOnProperty(name = "storage.type", havingValue = "cloud")
@Service
public class CloudStorageService implements StorageService {
    // Implementación para producción con Digital Ocean Spaces
}
```

### Validaciones Implementadas

| Validación | Regla |
|---|---|
| **Tamaño máximo** | 5 MB por archivo |
| **Formatos** | JPEG, PNG, WebP, GIF |
| **Límites USER** | Máximo 5 imágenes |
| **Límites ENTREPRENEURSHIP** | Máximo 10 imágenes |
| **Límites EVENT** | Máximo 20 imágenes |
| **Dimensiones** | Extraídas automáticamente de metadata |
| **Nombre único** | `{timestamp}_{uuid}.{extension}` |

### Endpoints del Sistema de Imágenes

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/images/upload` | Subir imagen (multipart) | Bearer |
| `GET` | `/api/images` | Listar por entidad | Público |
| `GET` | `/api/images/{id}` | Obtener imagen | Público |
| `PUT` | `/api/images/{id}` | Actualizar metadata | Bearer |
| `DELETE` | `/api/images/{id}` | Eliminar imagen | Bearer |
| `DELETE` | `/api/images` | Eliminar todas de entidad | Bearer |
| `POST` | `/api/images/reorder` | Reordenar galería | Bearer |
| `GET` | `/api/images/count` | Contar imágenes | Público |
| `GET` | `/api/images/can-add-more` | Verificar límite | Público |
| `GET` | `/api/files/**` | Servir archivo (local) | Público |

### Configuración por Perfil

**Local:**
```yaml
storage:
  type: local
  path: ./uploads
```

**Production:**
```yaml
storage:
  type: cloud
  do-spaces:
    endpoint: ${DO_SPACES_ENDPOINT}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}
    region: nyc3
```

---

## Seguridad – Zero Trust con Keycloak

Todos los microservicios son **OAuth2 Resource Servers independientes**. Cada servicio valida el JWT por sí mismo sin depender de un API Gateway.

### Flujo de Autenticación

```
Frontend (Angular)
     │ POST /login
     ▼
Keycloak (realm: emprendia)
     │ JWT Access Token
     ▼
Frontend
     │ Authorization: Bearer <token>
     ▼
Microservicio → valida firma, expiración, issuer, roles
     │ token válido    │ token inválido
     ▼                 ▼
  200 OK           401 Unauthorized (JSON)
```

### Modelo de Acceso por Servicio

| Servicio | Regla |
|---|---|
| **shared-service** | `GET` catalogues → **público** (sin token). `POST/PUT/DELETE` → requiere `ROLE_ADMIN` |
| **user-service** | Todo requiere token válido. Ownership con `@PreAuthorize` en service layer |
| **entrepreneurship-service** | Todo requiere token válido. Ownership con `@PreAuthorize` en service layer |
| **event-service** | Todo requiere token válido. Ownership con `@PreAuthorize` en service layer |

### JWT Claims Esperados (Keycloak)

```json
{
  "sub": "uuid-keycloak-user-id",
  "preferred_username": "kevin",
  "email": "kevin@mail.com",
  "realm_access": {
    "roles": ["USER", "ADMIN"]
  },
  "exp": 9999999999
}
```

### Clases de Seguridad

- **`KeycloakJwtAuthenticationConverter`** — extrae `realm_access.roles` y los mapea como `ROLE_<NAME>`
- **`SecurityUtil`** — `getCurrentKeycloakId()`, `getCurrentUsername()`, `hasRole()` desde el contexto de Spring Security
- **`SecurityConstants`** — constantes: `ROLE_ADMIN`, `ROLE_USER`, claim names (`sub`, `preferred_username`, `email`)

### Respuestas de Error de Seguridad

```json
// 401 Unauthorized
{ "status": 401, "error": "Unauthorized", "message": "..." }

// 403 Forbidden
{ "status": 403, "error": "Forbidden", "message": "..." }
```

### Configuración Keycloak

| Parámetro | Valor |
|---|---|
| URL local | `http://localhost:8080` |
| Realm | `emprendia` |
| JWKS URI | `http://localhost:8080/realms/emprendia/protocol/openid-connect/certs` |
| Variable de entorno | `KEYCLOAK_ISSUER_URI` |

---

## Perfiles de Despliegue

| Perfil | Descripción | Variables de Entorno |
|---|---|---|
| `local` | Desarrollo local con defaults | `DB_USERNAME` (default: postgres), `DB_PASSWORD` (default: postgres). Storage: `storage.type=local` |
| `dev` | Ambiente de desarrollo | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KEYCLOAK_ISSUER_URI`. Storage configurado según necesidad |
| `prod` | Producción | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KEYCLOAK_ISSUER_URI`. **shared-service**: `DO_SPACES_*` para cloud storage |

**Shared Service adicionales (prod):**
- `DO_SPACES_ENDPOINT` (ej: `https://nyc3.digitaloceanspaces.com`)
- `DO_SPACES_ACCESS_KEY`
- `DO_SPACES_SECRET_KEY`
- `DO_SPACES_BUCKET_NAME`

```bash
# Activar perfil via argumento
java -jar service.jar --spring.profiles.active=dev

# Activar perfil via variable de entorno
export SPRING_PROFILES_ACTIVE=dev
```

---

## Puertos

| Servicio | Puerto |
|---|---|
| user-service | 8081 |
| entrepreneurship-service | 8082 |
| event-service | 8083 |
| shared-service | 8084 |

---

## Ejecución Local

### Prerrequisitos
- Java 21
- PostgreSQL con base de datos `emprendia_db` (DDL aplicado desde `../db/DDL/` + DML desde `../db/DML/initial_data.sql`)
- Keycloak corriendo en `localhost:8080` con realm `emprendia`

### Variables de Entorno (Producción)

Para despliegue en **producción** con Digital Ocean Spaces, configurar las siguientes variables de entorno en **shared-service**:

```bash
export SPRING_PROFILES_ACTIVE=prod
export DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com
export DO_SPACES_ACCESS_KEY=your_access_key
export DO_SPACES_SECRET_KEY=your_secret_key
export DO_SPACES_BUCKET_NAME=emprendia-storage
export DB_URL=jdbc:postgresql://your-db-host:5432/emprendia_db
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export KEYCLOAK_ISSUER_URI=https://your-keycloak-server/realms/emprendia
```

> **Desarrollo local**: No requiere configurar variables de Digital Ocean. El perfil `local` usa `storage.type=local` con archivos en `./uploads/`.

### Desde el root (todos los servicios)
```bash
./gradlew build
./gradlew :user-service:bootRun --args='--spring.profiles.active=local'
./gradlew :shared-service:bootRun --args='--spring.profiles.active=local'
./gradlew :entrepreneurship-service:bootRun --args='--spring.profiles.active=local'
./gradlew :event-service:bootRun --args='--spring.profiles.active=local'
```

### Desde cada servicio (standalone)
```bash
cd user-service && gradle bootRun --args='--spring.profiles.active=local'
cd shared-service && gradle bootRun --args='--spring.profiles.active=local'
cd entrepreneurship-service && gradle bootRun --args='--spring.profiles.active=local'
cd event-service && gradle bootRun --args='--spring.profiles.active=local'
```

### Ejecutar tests
```bash
./gradlew test
# o por servicio:
cd user-service && gradle test
```

---

## Health Checks (Actuator)

```
GET /actuator/health   → UP/DOWN + detalle (local/dev)
GET /actuator/info
GET /actuator/metrics
```

En `prod` solo se expone `/actuator/health`.

---

## Convenciones de Código

| Elemento | Convención | Ejemplo |
|---|---|---|
| Entidades JPA | PascalCase en `domain/` | `AppUser.java` |
| Entidad polimórfica | Enum `entity_type` | `ImageGallery.java` con `EntityType` |
| DTO entrada | Sufijo `Request` | `UserRequest.java` |
| DTO salida | Sufijo `Response` | `UserResponse.java` |
| Servicio interfaz | PascalCase | `UserService.java` |
| Servicio impl | Sufijo `Impl` en `impl/` | `UserServiceImpl.java` |
| Storage service | `StorageService` interface + impls | `LocalStorageService.java`, `CloudStorageService.java` |
| Mapper | En `mapping/mapper/` | `UserMapper.java` |
| Repo QueryDSL | Sufijo `QueryRepository` | `UserQueryRepository.java` |
| Package base | `com.project.emprendia.{domain}` | `com.project.emprendia.user` |
| Package storage | `storage/` y `storage/impl/` | Solo en `shared-service` |

---

## Roadmap de Seguridad

| Fase | Estado | Descripción |
|---|---|---|
| Fase 1 | ✅ Implementado | Cada microservicio valida JWT directamente (zero-trust) |
| Fase 2 | 🔜 Pendiente | API Gateway centralizado (Spring Cloud Gateway / Kong) |
| Fase 3 | 🔜 Pendiente | Observability + service mesh + políticas avanzadas |

---

## Postman – Pruebas de API

Las colecciones de Postman están ubicadas en la carpeta `postman/` en la raíz del proyecto.

```
postman/
├── README.md                                       ← Guía completa de uso de colecciones
├── Emprendia.postman_environment.json              ← Variables de entorno (URLs, credenciales, token)
├── 00-Auth-Keycloak.postman_collection.json        ← Autenticación: Login, Refresh, Logout, Introspect
├── 01-shared-service.postman_collection.json       ← Shared Service: Catalogues + Image Gallery
├── 02-user-service.postman_collection.json         ← User Service: Users, Contacts, Addresses, Identifications
├── 03-entrepreneurship-service.postman_collection.json  ← Entrepreneurship Service: Emprendimientos + Categorías
└── 04-event-service.postman_collection.json        ← Event Service: Eventos, Espacios, Invitaciones
```

### Importación

1. Abrir Postman → **File → Import**
2. Arrastrar todos los archivos `.json` de la carpeta `postman/`
3. Seleccionar el environment **"Emprendia – Local"** en la esquina superior derecha de Postman

### Configuración del Environment

Editar las siguientes variables en `Emprendia.postman_environment.json` (o directamente en Postman):

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `keycloak_url` | `http://localhost:8080` | URL base de Keycloak |
| `keycloak_realm` | `emprendia` | Nombre del realm |
| `keycloak_client_id` | `emprendia-app` | Client ID con Direct Access Grants activado |
| `keycloak_username` | `tu_usuario` | Usuario de prueba en Keycloak |
| `keycloak_password` | `tu_password` | Contraseña del usuario |
| `access_token` | *(auto-relleno)* | JWT – se llenar automáticamente al hacer Login |
| `shared_url` | `http://localhost:8084` | Base URL del Shared Service |
| `user_url` | `http://localhost:8081` | Base URL del User Service |
| `entrepreneurship_url` | `http://localhost:8082` | Base URL del Entrepreneurship Service |
| `event_url` | `http://localhost:8083` | Base URL del Event Service |

### Flujo de Autenticación

El flujo recomendado para pruebas es el siguiente:

```
1. Ejecutar: 00-Auth-Keycloak → "Login – Obtener JWT"
        ↓
   Se guardan automáticamente en el environment:
   - access_token  (JWT válido, expira según config Keycloak, por defecto 5 min)
   - refresh_token (para renovar sin re-ingresar credenciales)
   - token_expires_at (timestamp ISO-8601 de expiración)
        ↓
2. Usar cualquier colección de servicios normalmente
   (todas envían Authorization: Bearer {{access_token}} automáticamente)
        ↓
3. Si el token expira → ejecutar "Refresh Token" o volver a hacer "Login"
```

> **Auto-fetch:** Si `access_token` está vacío cuando se lanza una petición en cualquiera de las colecciones de servicio, el pre-request script intenta obtener el token automáticamente usando las variables del environment, sin necesidad de ir a la colección de auth.

### Colecciones Disponibles

#### `00 – Auth Keycloak`
| Request | Descripción |
|---|---|
| Login – Obtener JWT | Password grant flow. Guarda `access_token` y `refresh_token` en el environment |
| Refresh Token | Renueva el `access_token` usando el `refresh_token` vigente |
| Logout (Revocar Token) | Revoca el token en Keycloak y limpia las variables del environment |
| Introspect Token (Validar JWT) | Muestra username, email, roles y expiración del token activo |

#### `01 – Shared Service` (`:8084`)
Los endpoints `GET` de catálogos son **públicos** (no requieren token). Los `POST/PUT/DELETE` requieren `ROLE_ADMIN`.

**Catalogue Types:**

| Request | Método | Endpoint |
|---|---|---|
| Listar tipos de catálogo | `GET` | `/api/v1/catalogue-types` |
| Obtener tipo por ID | `GET` | `/api/v1/catalogue-types/{id}` |
| Crear tipo | `POST` | `/api/v1/catalogue-types` |
| Actualizar tipo | `PUT` | `/api/v1/catalogue-types/{id}` |
| Eliminar tipo | `DELETE` | `/api/v1/catalogue-types/{id}` |

**Catalogue Values:**

| Request | Método | Endpoint |
|---|---|---|
| Valores por tipo (typeCode) | `GET` | `/api/v1/catalogue-values/by-type/{typeCode}` |
| Obtener valor por ID | `GET` | `/api/v1/catalogue-values/{id}` |
| Hijos de un valor | `GET` | `/api/v1/catalogue-values/{id}/children` |
| Crear valor | `POST` | `/api/v1/catalogue-values` |
| Actualizar valor | `PUT` | `/api/v1/catalogue-values/{id}` |
| Eliminar valor | `DELETE` | `/api/v1/catalogue-values/{id}` |

**Image Gallery** (Sistema de gestión de imágenes):

| Request | Método | Endpoint | Auth |
|---|---|---|---|
| Upload imagen | `POST` | `/api/images/upload` | Bearer |
| Listar por entidad | `GET` | `/api/images?entityType={type}&entityId={id}` | Público |
| Obtener imagen | `GET` | `/api/images/{imageId}` | Público |
| Actualizar metadata | `PUT` | `/api/images/{imageId}` | Bearer |
| Eliminar imagen | `DELETE` | `/api/images/{imageId}` | Bearer |
| Eliminar todas | `DELETE` | `/api/images?entityType={type}&entityId={id}` | Bearer |
| Reordenar imágenes | `POST` | `/api/images/reorder?entityType={type}&entityId={id}` | Bearer |
| Contar imágenes | `GET` | `/api/images/count?entityType={type}&entityId={id}` | Público |
| Puede agregar más | `GET` | `/api/images/can-add-more?entityType={type}&entityId={id}` | Público |
| Servir archivo (local) | `GET` | `/api/files/{entity}/{id}/{fileName}` | Público |

> **EntityType**: `USER`, `ENTREPRENEURSHIP`, `EVENT`  
> **Upload**: Multipart form-data con campos: `file`, `entityType`, `entityId`, `displayOrder`, `altText`, `description`, `uploadedByUserId`

#### `02 – User Service` (`:8081`)
Todos los endpoints requieren JWT válido.

**Users:**

| Request | Método | Endpoint |
|---|---|---|
| Listar todos los usuarios | `GET` | `/api/v1/users` |
| Obtener usuario por ID | `GET` | `/api/v1/users/{id}` |
| Obtener usuario por Keycloak ID | `GET` | `/api/v1/users/keycloak/{keycloakId}` |
| Crear usuario | `POST` | `/api/v1/users` |
| Actualizar usuario | `PUT` | `/api/v1/users/{id}` |
| Eliminar usuario | `DELETE` | `/api/v1/users/{id}` |

**User Contacts** (Teléfonos, emails):

| Request | Método | Endpoint |
|---|---|---|
| Listar contactos | `GET` | `/api/v1/user-contacts` |
| Obtener contacto | `GET` | `/api/v1/user-contacts/{id}` |
| Crear contacto | `POST` | `/api/v1/user-contacts` |
| Actualizar contacto | `PUT` | `/api/v1/user-contacts/{id}` |
| Eliminar contacto | `DELETE` | `/api/v1/user-contacts/{id}` |

**User Addresses** (Direcciones con jerarquía geográfica):

| Request | Método | Endpoint |
|---|---|---|
| Listar direcciones | `GET` | `/api/v1/user-addresses` |
| Obtener dirección | `GET` | `/api/v1/user-addresses/{id}` |
| Crear dirección | `POST` | `/api/v1/user-addresses` |
| Actualizar dirección | `PUT` | `/api/v1/user-addresses/{id}` |
| Eliminar dirección | `DELETE` | `/api/v1/user-addresses/{id}` |

**User Identifications** (Cédula, RUC, Pasaporte):

| Request | Método | Endpoint |
|---|---|---|
| Listar identificaciones | `GET` | `/api/v1/user-identifications` |
| Obtener identificación | `GET` | `/api/v1/user-identifications/{id}` |
| Crear identificación | `POST` | `/api/v1/user-identifications` |
| Actualizar identificación | `PUT` | `/api/v1/user-identifications/{id}` |
| Eliminar identificación | `DELETE` | `/api/v1/user-identifications/{id}` |

#### `03 – Entrepreneurship Service` (`:8082`)
Todos los endpoints requieren JWT válido.

**Entrepreneurships:**

| Request | Método | Endpoint |
|---|---|---|
| Listar todos | `GET` | `/api/v1/entrepreneurships` |
| Obtener por ID | `GET` | `/api/v1/entrepreneurships/{id}` |
| Listar por usuario | `GET` | `/api/v1/entrepreneurships/user/{userId}` |
| Búsqueda con filtros | `GET` | `/api/v1/entrepreneurships/search?name=&categoryId=&isPhysical=&isDigital=` |
| Crear emprendimiento | `POST` | `/api/v1/entrepreneurships` |
| Actualizar emprendimiento | `PUT` | `/api/v1/entrepreneurships/{id}` |
| Eliminar emprendimiento | `DELETE` | `/api/v1/entrepreneurships/{id}` |

**Categories:**

| Request | Método | Endpoint |
|---|---|---|
| Listar categorías | `GET` | `/api/v1/categories` |
| Obtener por ID | `GET` | `/api/v1/categories/{id}` |
| Obtener por nombre | `GET` | `/api/v1/categories/name/{name}` |
| Crear categoría | `POST` | `/api/v1/categories` |
| Actualizar categoría | `PUT` | `/api/v1/categories/{id}` |
| Eliminar categoría | `DELETE` | `/api/v1/categories/{id}` |

#### `04 – Event Service` (`:8083`)
Todos los endpoints requieren JWT válido. Incluye ejemplos de creación: evento presencial gratuito, evento virtual de pago.

**Events:**

| Request | Método | Endpoint |
|---|---|---|
| Listar todos | `GET` | `/api/v1/events` |
| Obtener por ID | `GET` | `/api/v1/events/{id}` |
| Listar por creador | `GET` | `/api/v1/events/creator/{userId}` |
| Búsqueda con filtros | `GET` | `/api/v1/events/search?name=&eventTypeId=&eventVisibilityId=&fromDate=&toDate=` |
| Crear evento presencial | `POST` | `/api/v1/events` |
| Crear evento virtual de pago | `POST` | `/api/v1/events` |
| Actualizar evento | `PUT` | `/api/v1/events/{id}` |
| Eliminar evento | `DELETE` | `/api/v1/events/{id}` |

**Event Spaces:**

| Request | Método | Endpoint |
|---|---|---|
| Listar espacios | `GET` | `/api/v1/events/{eventId}/spaces` |
| Crear espacio | `POST` | `/api/v1/event-spaces` |

**Event Invitations:**

| Request | Método | Endpoint |
|---|---|---|
| Listar invitaciones | `GET` | `/api/v1/events/{eventId}/invitations` |
| Crear invitación | `POST` | `/api/v1/event-invitations` |

### Prerrequisito Keycloak

Para que el login con usuario/contraseña funcione desde Postman, el client de Keycloak debe tener habilitado **Direct Access Grants**:

```
Keycloak Admin → Realm: emprendia → Clients → emprendia-app
    → Settings → Capability config
        → Direct access grants: ON
```

### Flujo de Prueba Completo

**1. Setup Inicial:**
```
00-Auth > Login
01-Shared > Catalogue Types > GET Listar todos
01-Shared > Catalogue Values > GET Por tipo (COUNTRY, CATEGORY, etc.)
```

**2. Crear Usuario con Imágenes:**
```
02-User > Users > POST Crear usuario
02-User > User Contacts > POST Crear contacto (teléfono)
02-User > User Addresses > POST Crear dirección
02-User > User Identifications > POST Crear identificación
01-Shared > Image Gallery > GET Can Add More (entityType=USER, entityId=1)
01-Shared > Image Gallery > Upload Image (seleccionar archivo)
01-Shared > Image Gallery > GET Images por Entidad
```

**3. Crear Emprendimiento con Galería:**
```
03-Entrepreneurship > POST Crear emprendimiento
01-Shared > Upload Image (entityType=ENTREPRENEURSHIP, entityId=1)
01-Shared > Upload Image (repetir hasta 10 imágenes)
01-Shared > Reorder Images (ajustar orden de galería)
```

**4. Crear Evento:**
```
04-Event > POST Crear evento
04-Event > POST Crear espacio
04-Event > POST Crear invitación
01-Shared > Upload Image (entityType=EVENT, entityId=1)
```

> **Nota**: Consulta `postman/README.md` para documentación detallada de cada endpoint, validaciones y troubleshooting.

---

## Autor

**Kevin Guachagmira**  
Email: mantillagka@gmail.com  
Fecha: Mayo 2026  
Proyecto: Plataforma Emprendia - Tesis NIBE
