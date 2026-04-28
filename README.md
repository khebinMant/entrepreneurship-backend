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
Frontend (Next.js)
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

## Autor

**Kevin Guachagmira**  
Email: mantillagka@gmail.com  
Fecha: Abril 2026  
Proyecto: Tesis
