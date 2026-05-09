# Guía del Desarrollador - Emprendia
**Versión**: 3.0 | **Fecha**: Mayo 8, 2026

---

## 📋 Contenido

1. [Compilación y Ejecución](#compilación-y-ejecución)
2. [Comunicación entre Microservicios](#comunicación-entre-microservicios)
3. [Circuit Breaker y Resiliencia](#circuit-breaker-y-resiliencia)
4. [Sistema de Imágenes](#sistema-de-imágenes)
5. [Configuración por Ambiente](#configuración-por-ambiente)
6. [Troubleshooting](#troubleshooting)

---

## 🚀 Compilación y Ejecución

### Prerrequisitos
- Java 21
- PostgreSQL (puerto 5433, BD: `emprendia_db`)
- Keycloak (puerto 8080, realm: `emprendia`)

### Compilar Todo
```powershell
cd C:\Users\KevinGuachagmira\Documents\Pichincha\nibe\Tesis\back\micros
./gradlew clean build -x test
```

### Ejecutar Servicios (en orden)

```powershell
# Terminal 1 - Shared Service (primero, otros dependen de él)
cd shared-service
./gradlew bootRun --args='--spring.profiles.active=local'

# Terminal 2 - User Service
cd user-service
./gradlew bootRun --args='--spring.profiles.active=local'

# Terminal 3 - Entrepreneurship Service
cd entrepreneurship-service
./gradlew bootRun --args='--spring.profiles.active=local'

# Terminal 4 - Event Service
cd event-service
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Verificar Salud
```powershell
curl http://localhost:8084/actuator/health  # Shared
curl http://localhost:8081/actuator/health  # User
curl http://localhost:8082/actuator/health  # Entrepreneurship
curl http://localhost:8083/actuator/health  # Event
```

---

## 🔗 Comunicación entre Microservicios

### Arquitectura

```
┌──────────────────┐
│  Event Service   │
│    Port 8083     │
└────────┬─────────┘
         │
         ├──────────► Shared Service (catálogos)
         ├──────────► User Service (organizadores)
         └──────────► Entrepreneurship Service (participantes)

┌───────────────────────┐
│ Entrepreneurship Svc  │
│    Port 8082          │
└────────┬──────────────┘
         │
         ├──────────► Shared Service (catálogos)
         └──────────► User Service (propietarios)

┌──────────────────┐
│   User Service   │
│    Port 8081     │
└────────┬─────────┘
         │
         └──────────► Shared Service (catálogos)

┌──────────────────┐
│  Shared Service  │
│    Port 8084     │
│ (Catálogos +     │
│  Imágenes)       │
└──────────────────┘
```

### Feign Clients Implementados

| Servicio | Client | Endpoint | Fallback |
|----------|--------|----------|----------|
| user-service | SharedServiceClient | `/api/v1/catalogue-values` | Lista vacía o "N/A" |
| entrepreneurship-service | SharedServiceClient | `/api/v1/catalogue-values` | Lista vacía o "N/A" |
| entrepreneurship-service | UserServiceClient | `/api/v1/users/{id}` | Usuario con "N/A" |
| event-service | SharedServiceClient | `/api/v1/catalogue-values` | Lista vacía o "N/A" |
| event-service | UserServiceClient | `/api/v1/users/{id}` | Usuario con "N/A" |
| event-service | EntrepreneurshipServiceClient | `/api/v1/entrepreneurships/{id}` | Emprendimiento con "N/A" |

### Ejemplo de Uso

```java
@Service
@RequiredArgsConstructor
public class EventServiceImpl {
    private final UserServiceClient userServiceClient;
    private final SharedServiceClient sharedServiceClient;
    
    public EventDetailResponse getById(Long id) {
        Event event = repository.findById(id).orElseThrow();
        
        // Obtener info del organizador
        UserBasicResponse organizer = userServiceClient.getUserById(event.getCreatedByUserId());
        
        // Obtener tipo de evento
        CatalogueValueResponse eventType = sharedServiceClient.getValueById(event.getEventTypeId());
        
        return EventDetailResponse.builder()
            .eventId(event.getId())
            .name(event.getName())
            .organizerName(organizer.getFirstName() + " " + organizer.getLastName())
            .eventTypeName(eventType.getName())
            .build();
    }
}
```

---

## 🛡️ Circuit Breaker y Resiliencia

### Configuración (application.yml)

```yaml
# Feign Client
feign:
  client:
    config:
      default:
        connectTimeout: 5000      # 5s para conectar
        readTimeout: 10000        # 10s para leer
        loggerLevel: BASIC
  circuitbreaker:
    enabled: true

# Services URLs
services:
  shared:
    url: ${SHARED_SERVICE_URL:http://localhost:8084}
  user:
    url: ${USER_SERVICE_URL:http://localhost:8081}
  entrepreneurship:
    url: ${ENTREPRENEURSHIP_SERVICE_URL:http://localhost:8082}

# Resilience4j Circuit Breaker
resilience4j:
  circuitbreaker:
    instances:
      sharedService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 10s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
  timelimiter:
    instances:
      sharedService:
        timeoutDuration: 10s
```

### Estados del Circuit Breaker

```
CLOSED (Normal) ──50% fallos──► OPEN (Bloqueado) ──10s──► HALF_OPEN (Probando) ──3 éxitos──► CLOSED
```

### Fallback Methods

```java
@FeignClient(name = "shared-service", url = "${services.shared.url}")
public interface SharedServiceClient {
    
    @GetMapping("/api/v1/catalogue-values/{id}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValueByIdFallback")
    CatalogueValueResponse getValueById(@PathVariable Long id);
    
    // Fallback: mismo signature + Throwable
    default CatalogueValueResponse getValueByIdFallback(Long id, Throwable t) {
        return CatalogueValueResponse.builder()
            .catalogueValueId(id)
            .name("N/A")
            .code("N/A")
            .build();
    }
}
```

### Métricas de Circuit Breaker

```bash
curl http://localhost:8083/actuator/health
```

**Respuesta:**
```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP",
      "details": {
        "sharedService": {
          "status": "CLOSED",
          "failureRate": "0.0%"
        }
      }
    }
  }
}
```

---

## 📸 Sistema de Imágenes

### Arquitectura de Storage

```
Strategy Pattern
     │
     ▼
StorageService (interface)
     │
     ├──► LocalStorageService      (@ConditionalOnProperty: storage.type=local)
     │    └── ./uploads/
     │
     └──► CloudStorageService      (@ConditionalOnProperty: storage.type=cloud)
          └── Digital Ocean Spaces
```

### Límites por Entidad

| Entidad | Límite |
|---------|--------|
| USER | 5 imágenes |
| ENTREPRENEURSHIP | 10 imágenes |
| EVENT | 20 imágenes |

### Validaciones

- **Max size**: 5 MB
- **Formatos**: JPEG, PNG, WebP, GIF
- **Dimensiones**: Extraídas automáticamente
- **Nombre**: `{timestamp}_{uuid}.{extension}`

### Estructura de Rutas

```
uploads/ (o bucket en cloud)
├── users/{userId}/
│   ├── profile.jpg                      ← displayOrder=0
│   └── gallery/20260508_143022_abc.jpg  ← displayOrder>0
├── entrepreneurships/{id}/
│   ├── logo.jpg
│   └── gallery/20260508_150000_ghi.jpg
└── events/{id}/
    ├── cover.jpg
    └── gallery/20260508_160000_mno.jpg
```

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/images/upload` | Subir imagen (multipart) |
| GET | `/api/images?entityType=X&entityId=Y` | Listar imágenes |
| DELETE | `/api/images/{id}` | Eliminar imagen |
| POST | `/api/images/reorder` | Reordenar galería |
| GET | `/api/images/can-add-more` | Verificar límite |

### Ejemplo de Upload (cURL)

```bash
curl -X POST http://localhost:8084/api/images/upload \
  -F "file=@product.jpg" \
  -F "entityType=ENTREPRENEURSHIP" \
  -F "entityId=1" \
  -F "displayOrder=1" \
  -F "altText=Producto artesanal"
```

---

## ⚙️ Configuración por Ambiente

### Local (Desarrollo)

```yaml
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:postgresql://localhost:5433/postgres
    username: postgres
    password: emprendia2026

storage:
  type: local
  local:
    path: ./uploads
    base-url: http://localhost:8084/api/files
```

### Producción

```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

storage:
  type: cloud
  cloud:
    endpoint: ${DO_SPACES_ENDPOINT}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}
```

### Variables de Entorno (Producción)

```bash
# Database
export DB_URL=jdbc:postgresql://prod-host:5432/emprendia_db
export DB_USERNAME=emprendia_user
export DB_PASSWORD=secure_password

# Keycloak
export KEYCLOAK_ISSUER_URI=https://keycloak.prod.com/realms/emprendia

# Services (para Feign Clients)
export SHARED_SERVICE_URL=http://shared-service:8084
export USER_SERVICE_URL=http://user-service:8081
export ENTREPRENEURSHIP_SERVICE_URL=http://entrepreneurship-service:8082

# Digital Ocean Spaces (solo shared-service)
export DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com
export DO_SPACES_ACCESS_KEY=your-key
export DO_SPACES_SECRET_KEY=your-secret
export DO_SPACES_BUCKET_NAME=emprendia-bucket
```

---

## 🐛 Troubleshooting

### Error: "Could not find spring-cloud-starter-openfeign"

**Causa**: Falta Spring Cloud BOM

**Solución**: Verificar en `build.gradle`:
```gradle
dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-dependencies:2023.0.3'
    }
}
```

### Error: "CircuitBreaker is OPEN"

**Causa**: Demasiados fallos en el servicio destino

**Solución**:
1. Verificar que el servicio esté corriendo
2. Esperar 10 segundos para auto-recuperación
3. Revisar logs del servicio destino

### Error: "File size exceeds maximum"

**Causa**: Archivo mayor a 5MB

**Solución**: Comprimir imagen antes de subir

### Error: "Image limit reached"

**Causa**: Límite alcanzado (USER=5, ENTREPRENEURSHIP=10, EVENT=20)

**Solución**: Eliminar imágenes existentes primero

### Error: Connection Refused

**Causa**: Servicio no está corriendo

**Solución**:
```powershell
# Verificar servicios
curl http://localhost:8084/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

---

## 📊 Puertos de Servicios

| Servicio | Puerto | Health Check |
|----------|--------|--------------|
| Keycloak | 8080 | - |
| User | 8081 | `/actuator/health` |
| Entrepreneurship | 8082 | `/actuator/health` |
| Event | 8083 | `/actuator/health` |
| Shared | 8084 | `/actuator/health` |
| PostgreSQL | 5433 | - |

---

## 🔧 Dependencias Clave

```gradle
dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    
    // OpenFeign + Circuit Breaker
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-circuitbreaker:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-feign:2.2.0'
    
    // QueryDSL
    implementation 'com.querydsl:querydsl-jpa:5.1.0:jakarta'
    
    // MapStruct
    implementation 'org.mapstruct:mapstruct:1.6.3'
    
    // PostgreSQL
    runtimeOnly 'org.postgresql:postgresql'
}

dependencyManagement {
    imports {
        mavenBom 'org.springframework.cloud:spring-cloud-dependencies:2023.0.3'
    }
}
```

---

## 📝 Mejores Prácticas

### 1. Siempre usar Fallbacks
```java
default UserBasicResponse getUserByIdFallback(Long id, Throwable t) {
    log.warn("User service unavailable for userId: {}", id);
    return UserBasicResponse.builder()
        .userId(id)
        .firstName("N/A")
        .lastName("N/A")
        .build();
}
```

### 2. Logging de Feign Calls
```yaml
logging:
  level:
    com.project.emprendia: DEBUG
```

### 3. Verificar Límites antes de Upload
```bash
GET /api/images/can-add-more?entityType=USER&entityId=1
# Response: true (puede agregar) o false (límite alcanzado)
```

### 4. Timeout Apropiados
- `connectTimeout: 5000` (5s para conectar)
- `readTimeout: 10000` (10s para operaciones normales)
- Para operaciones pesadas, aumentar `readTimeout`

### 5. Health Checks
Configurar en todos los servicios:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

---

## 🎯 Checklist de Deploy

### Pre-Deploy
- [ ] Todos los servicios compilan sin errores
- [ ] Tests pasan (`./gradlew test`)
- [ ] PostgreSQL inicializada con DDL + DML
- [ ] Keycloak configurado (realm + client)
- [ ] Variables de entorno configuradas

### Deploy
- [ ] Ejecutar servicios en orden: shared → user → entrepreneurship → event
- [ ] Verificar health checks
- [ ] Probar con Postman: Auth → Endpoints básicos
- [ ] Verificar logs (no errores críticos)

### Post-Deploy
- [ ] Subir imagen de prueba
- [ ] Verificar Feign Clients funcionan
- [ ] Probar Circuit Breaker (detener servicio y ver fallback)
- [ ] Monitorear métricas en `/actuator`

---

**Autor**: Kevin Guachagmira  
**Email**: kguachag@pichincha.com  
**Proyecto**: Plataforma Emprendia - Tesis NIBE  
**Versión**: 3.0 | Mayo 8, 2026

