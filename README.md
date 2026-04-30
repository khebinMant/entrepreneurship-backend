# Emprendia - Plataforma de Emprendimientos

## Descripción General

Emprendia es una plataforma de **microservicios independientes** construida con **Spring Boot 3.5.7** para gestionar emprendimientos, eventos y la participación de negocios en ferias y exposiciones. Cada microservicio es desplegable de forma autónoma, con su propio `settings.gradle` y `build.gradle` completo.

La base de datos está documentada en [`../db/tesis_db.md`](../db/tesis_db.md).

---

## Arquitectura

```
micros/                              ← Multi-project root (build/deploy conveniente)
├── settings.gradle                  ← Une los 4 proyectos para build conjunto
├── build.gradle                     ← Solo declara group/version, sin subprojects{}
│
├── shared-service/                  → Puerto 8084 | Catálogos y valores compartidos
│   ├── settings.gradle              ← Standalone: rootProject.name = 'shared-service'
│   └── build.gradle                 ← Standalone: todas las dependencias declaradas
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

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Spring Boot | 3.5.7 | Framework base |
| Spring Web | - | REST API |
| Spring Data JPA | - | Persistencia |
| Spring Security + OAuth2 Resource Server | - | Autenticación JWT |
| Keycloak | - | Identity Provider (realm: `emprendia`) |
| QueryDSL | 5.1.0 | Consultas tipadas (`BooleanBuilder`, `JPAQueryFactory`) |
| MapStruct | 1.6.3 | Mapeo de objetos (`@Mapper`, `@BeanMapping`) |
| Lombok | - | Reducción de boilerplate |
| Apache Commons Text | 1.12.0 | Utilidades de texto (`CaseUtils`) |
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
| `local` | Desarrollo local con defaults | `DB_USERNAME` (default: postgres), `DB_PASSWORD` (default: postgres) |
| `dev` | Ambiente de desarrollo | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KEYCLOAK_ISSUER_URI` |
| `prod` | Producción | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KEYCLOAK_ISSUER_URI` |

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
| DTO entrada | Sufijo `Request` | `UserRequest.java` |
| DTO salida | Sufijo `Response` | `UserResponse.java` |
| Servicio interfaz | PascalCase | `UserService.java` |
| Servicio impl | Sufijo `Impl` en `impl/` | `UserServiceImpl.java` |
| Mapper | En `mapping/mapper/` | `UserMapper.java` |
| Repo QueryDSL | Sufijo `QueryRepository` | `UserQueryRepository.java` |
| Package base | `com.project.emprendia.{domain}` | `com.project.emprendia.user` |

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
├── Emprendia.postman_environment.json              ← Variables de entorno (URLs, credenciales, token)
├── 00-Auth-Keycloak.postman_collection.json        ← Autenticación: Login, Refresh, Logout, Introspect
├── 01-shared-service.postman_collection.json       ← Shared Service: CatalogueTypes + CatalogueValues
├── 02-user-service.postman_collection.json         ← User Service: CRUD de usuarios
├── 03-entrepreneurship-service.postman_collection.json  ← Entrepreneurship Service: CRUD de emprendimientos
└── 04-event-service.postman_collection.json        ← Event Service: CRUD de eventos
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

| Request | Método | Endpoint |
|---|---|---|
| Listar tipos de catálogo | `GET` | `/api/v1/catalogue-types` |
| Obtener tipo por ID | `GET` | `/api/v1/catalogue-types/{id}` |
| Crear tipo | `POST` | `/api/v1/catalogue-types` |
| Actualizar tipo | `PUT` | `/api/v1/catalogue-types/{id}` |
| Eliminar tipo | `DELETE` | `/api/v1/catalogue-types/{id}` |
| Valores por tipo (typeCode) | `GET` | `/api/v1/catalogue-values/by-type/{typeCode}` |
| Obtener valor por ID | `GET` | `/api/v1/catalogue-values/{id}` |
| Hijos de un valor | `GET` | `/api/v1/catalogue-values/{id}/children` |
| Crear valor | `POST` | `/api/v1/catalogue-values` |
| Actualizar valor | `PUT` | `/api/v1/catalogue-values/{id}` |
| Eliminar valor | `DELETE` | `/api/v1/catalogue-values/{id}` |

#### `02 – User Service` (`:8081`)
Todos los endpoints requieren JWT válido.

| Request | Método | Endpoint |
|---|---|---|
| Listar todos los usuarios | `GET` | `/api/v1/users` |
| Obtener usuario por ID | `GET` | `/api/v1/users/{id}` |
| Obtener usuario por Keycloak ID | `GET` | `/api/v1/users/keycloak/{keycloakId}` |
| Crear usuario | `POST` | `/api/v1/users` |
| Actualizar usuario | `PUT` | `/api/v1/users/{id}` |
| Eliminar usuario | `DELETE` | `/api/v1/users/{id}` |

#### `03 – Entrepreneurship Service` (`:8082`)
Todos los endpoints requieren JWT válido.

| Request | Método | Endpoint |
|---|---|---|
| Listar todos | `GET` | `/api/v1/entrepreneurships` |
| Obtener por ID | `GET` | `/api/v1/entrepreneurships/{id}` |
| Listar por usuario | `GET` | `/api/v1/entrepreneurships/user/{userId}` |
| Búsqueda con filtros | `GET` | `/api/v1/entrepreneurships/search?name=&categoryId=&isPhysical=&isDigital=` |
| Crear emprendimiento | `POST` | `/api/v1/entrepreneurships` |
| Actualizar emprendimiento | `PUT` | `/api/v1/entrepreneurships/{id}` |
| Eliminar emprendimiento | `DELETE` | `/api/v1/entrepreneurships/{id}` |

#### `04 – Event Service` (`:8083`)
Todos los endpoints requieren JWT válido. Incluye dos ejemplos de creación: evento presencial gratuito y evento virtual de pago.

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

### Prerrequisito Keycloak

Para que el login con usuario/contraseña funcione desde Postman, el client de Keycloak debe tener habilitado **Direct Access Grants**:

```
Keycloak Admin → Realm: emprendia → Clients → emprendia-app
    → Settings → Capability config
        → Direct access grants: ON
```

---

## Autor

**Kevin Guachagmira**  
Email: mantillagka@gmail.com  
Fecha: Abril 2026  
Proyecto: Tesis
