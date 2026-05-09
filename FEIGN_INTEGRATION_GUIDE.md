# Guía de Integración entre Microservicios
## Feign Client + Circuit Breaker (Resilience4j)

**Fecha**: Mayo 8, 2026  
**Proyecto**: Plataforma Emprendia - Comunicación entre Microservicios  
**Estado**: ✅ Implementado

---

## 📋 Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Arquitectura de Comunicación](#arquitectura-de-comunicación)
3. [Dependencias Agregadas](#dependencias-agregadas)
4. [Clientes Feign Creados](#clientes-feign-creados)
5. [Configuración Circuit Breaker](#configuración-circuit-breaker)
6. [DTOs de Intercambio](#dtos-de-intercambio)
7. [Uso en Servicios](#uso-en-servicios)
8. [Colecciones Postman](#colecciones-postman)
9. [Testing](#testing)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Resumen Ejecutivo

Se ha implementado **comunicación síncrona entre microservicios** utilizando:

- ✅ **OpenFeign** - Cliente HTTP declarativo para llamadas REST
- ✅ **Resilience4j Circuit Breaker** - Patrón de tolerancia a fallos
- ✅ **Fallback methods** - Respuestas alternativas cuando un servicio falla
- ✅ **DTOs compartidos** - Estructuras de datos comunes
- ✅ **Colección Postman** - Gestión de imágenes para todos los servicios

---

## 🏗️ Arquitectura de Comunicación

### Flujo de Comunicación

```
┌─────────────────┐
│  EVENT SERVICE  │
│   (Port 8083)   │
└────────┬────────┘
         │
         ├──────────► SHARED SERVICE (8084) - Catálogos
         │
         ├──────────► USER SERVICE (8081) - Usuarios
         │
         └──────────► ENTREPRENEURSHIP SERVICE (8082) - Emprendimientos
                              │
                              ├──────────► SHARED SERVICE (8084)
                              │
                              └──────────► USER SERVICE (8081)


┌─────────────────┐
│  USER SERVICE   │
│   (Port 8081)   │
└────────┬────────┘
         │
         └──────────► SHARED SERVICE (8084) - Catálogos
```

### Casos de Uso de Comunicación

| Servicio Origen | Servicio Destino | Información Requerida |
|-----------------|------------------|----------------------|
| **user-service** | shared-service | Catálogos (países, tipos de contacto, identificación) |
| **entrepreneurship-service** | shared-service | Catálogos (categorías, ubicaciones, redes sociales) |
| **entrepreneurship-service** | user-service | Información básica del propietario |
| **event-service** | shared-service | Catálogos (tipos de evento, ubicaciones, estados) |
| **event-service** | user-service | Información del organizador |
| **event-service** | entrepreneurship-service | Información de emprendimientos participantes |
| **Todos** | shared-service | Gestión de imágenes (upload, get, delete) |

---

## 📦 Dependencias Agregadas

### build.gradle (para user, entrepreneurship y event services)

```gradle
dependencies {
    // ...existing dependencies...
    
    // OpenFeign for inter-service communication
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'

    // Resilience4j for Circuit Breaker
    implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-circuitbreaker:2.2.0'
    implementation 'io.github.resilience4j:resilience4j-feign:2.2.0'
}
```

**Nota**: No se necesita agregar `dependencyManagement` para Spring Cloud ya que Resilience4j funciona de forma independiente.

---

## 🔌 Clientes Feign Creados

### 1. USER SERVICE

#### `SharedServiceClient.java`

```java
@FeignClient(
    name = "shared-service",
    url = "${services.shared.url}",
    path = "/api/v1"
)
public interface SharedServiceClient {
    
    @GetMapping("/catalogue-values/by-type/{typeCode}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValuesByTypeFallback")
    List<CatalogueValueResponse> getValuesByType(@PathVariable String typeCode);
    
    @GetMapping("/catalogue-values/{id}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValueByIdFallback")
    CatalogueValueResponse getValueById(@PathVariable Long id);
    
    // Fallback methods
    default List<CatalogueValueResponse> getValuesByTypeFallback(String typeCode, Throwable t) {
        return Collections.emptyList();
    }
    
    default CatalogueValueResponse getValueByIdFallback(Long id, Throwable t) {
        return CatalogueValueResponse.builder()
            .catalogueValueId(id)
            .name("N/A")
            .code("N/A")
            .build();
    }
}
```

**Uso en servicio:**
```java
@Service
@RequiredArgsConstructor
public class UserContactServiceImpl implements UserContactService {
    
    private final SharedServiceClient sharedServiceClient;
    
    public UserContactResponse create(UserContactRequest request) {
        // Validar tipo de contacto consultando shared-service
        CatalogueValueResponse contactType = sharedServiceClient.getValueById(request.getContactTypeId());
        
        if ("N/A".equals(contactType.getName())) {
            throw new BusinessException("Tipo de contacto no válido");
        }
        
        // ... resto de la lógica
    }
}
```

---

### 2. ENTREPRENEURSHIP SERVICE

#### `SharedServiceClient.java`

Similar al de user-service, consume catálogos.

#### `UserServiceClient.java`

```java
@FeignClient(
    name = "user-service",
    url = "${services.user.url}",
    path = "/api/v1"
)
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    UserBasicResponse getUserById(@PathVariable Long id);
    
    default UserBasicResponse getUserByIdFallback(Long id, Throwable t) {
        return UserBasicResponse.builder()
            .userId(id)
            .firstName("N/A")
            .lastName("N/A")
            .build();
    }
}
```

**Uso en servicio:**
```java
@Service
@RequiredArgsConstructor
public class EntrepreneurshipServiceImpl implements EntrepreneurshipService {
    
    private final UserServiceClient userServiceClient;
    private final SharedServiceClient sharedServiceClient;
    
    public EntrepreneurshipDetailResponse getById(Long id) {
        Entrepreneurship entrepreneurship = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Emprendimiento no encontrado"));
        
        // Obtener información del propietario
        UserBasicResponse owner = userServiceClient.getUserById(entrepreneurship.getUserId());
        
        // Obtener información de categoría
        CatalogueValueResponse category = sharedServiceClient.getValueById(entrepreneurship.getCategoryId());
        
        return EntrepreneurshipDetailResponse.builder()
            .entrepreneurshipId(entrepreneurship.getId())
            .name(entrepreneurship.getName())
            .ownerName(owner.getFirstName() + " " + owner.getLastName())
            .categoryName(category.getName())
            .build();
    }
}
```

---

### 3. EVENT SERVICE

#### `SharedServiceClient.java`

Consume catálogos de shared-service.

#### `UserServiceClient.java`

Obtiene información del organizador.

#### `EntrepreneurshipServiceClient.java`

```java
@FeignClient(
    name = "entrepreneurship-service",
    url = "${services.entrepreneurship.url}",
    path = "/api/v1"
)
public interface EntrepreneurshipServiceClient {
    
    @GetMapping("/entrepreneurships/{id}")
    @CircuitBreaker(name = "entrepreneurshipService", fallbackMethod = "getEntrepreneurshipByIdFallback")
    EntrepreneurshipBasicResponse getEntrepreneurshipById(@PathVariable Long id);
    
    default EntrepreneurshipBasicResponse getEntrepreneurshipByIdFallback(Long id, Throwable t) {
        return EntrepreneurshipBasicResponse.builder()
            .entrepreneurshipId(id)
            .name("N/A")
            .build();
    }
}
```

**Uso en servicio:**
```java
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    
    private final UserServiceClient userServiceClient;
    private final EntrepreneurshipServiceClient entrepreneurshipServiceClient;
    private final SharedServiceClient sharedServiceClient;
    
    public EventDetailResponse getById(Long id) {
        Event event = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        
        // Obtener organizador
        UserBasicResponse organizer = userServiceClient.getUserById(event.getCreatedByUserId());
        
        // Obtener tipo de evento
        CatalogueValueResponse eventType = sharedServiceClient.getValueById(event.getEventTypeId());
        
        // Obtener emprendimientos participantes
        List<EventParticipant> participants = participantRepository.findByEventId(id);
        List<EntrepreneurshipBasicResponse> entrepreneurships = participants.stream()
            .map(p -> entrepreneurshipServiceClient.getEntrepreneurshipById(p.getEntrepreneurshipId()))
            .collect(Collectors.toList());
        
        return EventDetailResponse.builder()
            .eventId(event.getId())
            .name(event.getName())
            .organizerName(organizer.getFirstName() + " " + organizer.getLastName())
            .eventTypeName(eventType.getName())
            .participants(entrepreneurships)
            .build();
    }
}
```

---

## ⚙️ Configuración Circuit Breaker

### application.yml (ejemplo para event-service)

```yaml
# Feign Client Configuration
feign:
  client:
    config:
      default:
        connectTimeout: 5000      # 5 segundos para conectar
        readTimeout: 10000        # 10 segundos para leer respuesta
        loggerLevel: BASIC        # NONE, BASIC, HEADERS, FULL
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
        slidingWindowSize: 10               # Muestras para calcular tasa de fallo
        minimumNumberOfCalls: 5             # Mínimo de llamadas antes de evaluar
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 10s        # Tiempo esperando antes de intentar de nuevo
        failureRateThreshold: 50            # % de fallos para abrir circuito
        eventConsumerBufferSize: 10
      userService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 10s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
      entrepreneurshipService:
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
      userService:
        timeoutDuration: 10s
      entrepreneurshipService:
        timeoutDuration: 10s
```

### Estados del Circuit Breaker

```
CLOSED (Normal) 
    ↓ 50% de fallos en últimas 10 llamadas
OPEN (Rechazando llamadas, usando fallback)
    ↓ Espera 10 segundos
HALF_OPEN (Probando si el servicio recuperó)
    ↓ 3 llamadas exitosas
CLOSED (Vuelve a normal)
```

---

## 📄 DTOs de Intercambio

### CatalogueValueResponse

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueValueResponse {
    private Long catalogueValueId;
    private String code;
    private String name;
    private String description;
    private Long parentValueId;
    private String parentValueName;
    private Long catalogueTypeId;
    private String catalogueTypeCode;
}
```

### UserBasicResponse

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicResponse {
    private Long userId;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
}
```

### EntrepreneurshipBasicResponse

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipBasicResponse {
    private Long entrepreneurshipId;
    private Long userId;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isPhysical;
    private Boolean isDigital;
}
```

---

## 📮 Colecciones Postman

### Nueva Colección: Image Management

**Archivo**: `05-image-management.postman_collection.json`

**Contenido:**

#### 1. USER Images
- ✅ Upload User Profile Image (displayOrder=0)
- ✅ Upload User Gallery Image (certificados, premios)
- ✅ Get User Images
- ✅ Can User Add More Images (límite: 5)
- ✅ Delete User Image

#### 2. ENTREPRENEURSHIP Images
- ✅ Upload Entrepreneurship Logo (displayOrder=0)
- ✅ Upload Product Image (galería de productos)
- ✅ Get Entrepreneurship Images
- ✅ Reorder Product Images
- ✅ Can Add More Images (límite: 10)
- ✅ Delete All Images

#### 3. EVENT Images
- ✅ Upload Event Cover (displayOrder=0)
- ✅ Upload Event Gallery Image (momentos, espacios)
- ✅ Get Event Images
- ✅ Get Event Image Count
- ✅ Can Add More Images (límite: 20)
- ✅ Update Image Metadata

#### 4. Local Storage Only
- ✅ GET User Profile Image File
- ✅ GET Entrepreneurship Logo File
- ✅ GET Event Cover File

**Nota**: Los endpoints de descarga directa solo funcionan con `storage.type=local`. En producción (Digital Ocean), las URLs son públicas.

---

## 🧪 Testing

### Test de Circuit Breaker

**Escenario 1: Servicio caído**

1. Detener shared-service
2. Llamar a user-service → `/api/v1/users/1`
3. Verificar que devuelve datos con catálogos en "N/A"
4. Verificar logs: "Circuit Breaker fallback activado"

**Escenario 2: Servicio lento**

1. Agregar `Thread.sleep(15000)` en shared-service
2. Llamar a event-service → `/api/v1/events/1`
3. Después de 10 segundos (timeout), verá fallback
4. Circuito se abre tras 5+ fallos

**Escenario 3: Recuperación**

1. Reiniciar shared-service
2. Esperar 10 segundos (waitDurationInOpenState)
3. Circuit Breaker pasa a HALF_OPEN
4. Tras 3 llamadas exitosas, vuelve a CLOSED

### Health Check con Circuit Breaker

```bash
curl http://localhost:8083/actuator/health
```

**Respuesta esperada:**

```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP",
      "details": {
        "sharedService": {
          "status": "CLOSED",
          "failureRate": "0.0%",
          "slowCallRate": "0.0%"
        },
        "userService": {
          "status": "CLOSED",
          "failureRate": "0.0%",
          "slowCallRate": "0.0%"
        }
      }
    }
  }
}
```

---

## 🔧 Troubleshooting

### Error: "No Feign Client for loadBalancing defined"

**Solución**: Asegurarse de que `url` esté configurado en `@FeignClient`:

```java
@FeignClient(
    name = "shared-service",
    url = "${services.shared.url}",  // ← IMPORTANTE
    path = "/api/v1"
)
```

### Error: "CircuitBreaker 'sharedService' is OPEN"

**Causa**: Demasiados fallos en el servicio destino.

**Solución**:
1. Verificar que el servicio destino esté corriendo
2. Esperar `waitDurationInOpenState` (10 segundos)
3. El circuito intentará automáticamente recuperarse

### Error: "Read timed out"

**Causa**: El servicio destino tarda más de 10 segundos.

**Solución**: Aumentar `readTimeout` en `application.yml`:

```yaml
feign:
  client:
    config:
      default:
        readTimeout: 20000  # 20 segundos
```

### Error: Fallback no se ejecuta

**Causa**: El método de fallback debe tener la misma firma + `Throwable`.

**Correcto**:
```java
CatalogueValueResponse getValueById(@PathVariable Long id);

default CatalogueValueResponse getValueByIdFallback(Long id, Throwable t) {
    // ...
}
```

**Incorrecto**:
```java
default CatalogueValueResponse getValueByIdFallback(Long id) {  // ← Falta Throwable
    // ...
}
```

---

## 📊 Métricas de Uso

### Monitoreo de Circuit Breaker

Activar endpoints de actuator en `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,circuitbreakers
  endpoint:
    health:
      show-details: always
```

**Endpoints de métricas:**

- `/actuator/circuitbreakers` - Estado de todos los circuit breakers
- `/actuator/circuitbreakerevents` - Eventos recientes (apertura, cierre)
- `/actuator/metrics/resilience4j.circuitbreaker.calls` - Contador de llamadas

---

## 🎯 Mejores Prácticas

### 1. Fallbacks Informativos

```java
default UserBasicResponse getUserByIdFallback(Long id, Throwable t) {
    log.warn("User service unavailable for userId: {}. Error: {}", id, t.getMessage());
    
    return UserBasicResponse.builder()
        .userId(id)
        .firstName("Usuario")
        .lastName("No Disponible")
        .profilePictureUrl(null)
        .build();
}
```

### 2. Cache en Fallback (Opcional)

```java
@Service
@RequiredArgsConstructor
public class UserServiceClientWrapper {
    
    private final UserServiceClient client;
    private final CacheManager cacheManager;
    
    public UserBasicResponse getUserById(Long id) {
        try {
            UserBasicResponse response = client.getUserById(id);
            // Guardar en cache
            cacheManager.put("users", id, response);
            return response;
        } catch (Exception e) {
            // Intentar recuperar de cache
            UserBasicResponse cached = cacheManager.get("users", id);
            return cached != null ? cached : createFallbackResponse(id);
        }
    }
}
```

### 3. Logging Estructurado

```java
@Slf4j
@Component
public class FeignClientLogger {
    
    @Around("@within(org.springframework.cloud.openfeign.FeignClient)")
    public Object logFeignCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            
            log.info("Feign call success: {} in {}ms", 
                joinPoint.getSignature().getName(), duration);
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            
            log.error("Feign call failed: {} after {}ms. Error: {}", 
                joinPoint.getSignature().getName(), duration, e.getMessage());
            
            throw e;
        }
    }
}
```

---

## 🚀 Próximos Pasos

### Corto Plazo

1. ✅ Implementar endpoints que usen los Feign Clients
2. ✅ Testing de integración entre servicios
3. ⏳ Agregar validaciones de negocio con datos de catálogos
4. ⏳ Enriquecer responses con información de otros servicios

### Medio Plazo

5. ⏳ Implementar cache distribuido (Redis) para catálogos
6. ⏳ Rate limiting en llamadas entre servicios
7. ⏳ Métricas avanzadas con Micrometer + Prometheus
8. ⏳ Tracing distribuido con Spring Cloud Sleuth

### Largo Plazo

9. ⏳ Service Mesh (Istio/Linkerd) para comunicación avanzada
10. ⏳ API Gateway centralizado
11. ⏳ Event-driven architecture para reducir llamadas síncronas
12. ⏳ GraphQL Federation para consultas complejas

---

## 📝 Conclusión

Se ha implementado exitosamente:

✅ **Comunicación robusta** entre microservicios con Feign Client  
✅ **Tolerancia a fallos** con Circuit Breaker y fallbacks  
✅ **Colección Postman** completa para gestión de imágenes  
✅ **Configuración flexible** por ambiente (local, dev, prod)  
✅ **Monitoreo** de health y métricas de circuitos  

El sistema está **preparado para producción** y puede escalar agregando más instancias de cada servicio sin cambios en la configuración.

---

**Desarrollado por:** Kevin Guachagmira  
**Email:** kguachag@pichincha.com  
**Fecha de Implementación:** Mayo 8, 2026  
**Versión:** 1.0.0

