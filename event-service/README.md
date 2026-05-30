# Event Service - Dominio de Eventos

## Descripción

Microservicio que gestiona eventos (ferias, exposiciones), espacios disponibles, invitaciones a emprendimientos y el seguimiento de participación en los eventos.

**Integración con Shared Service**: Obtiene automáticamente la imagen de portada del evento desde el servicio de imágenes.

## Puerto: `8083`
## Package Base: `com.project.emprendia.event`

---

## Dominio

### Entidades

| Entidad | Tabla | Descripción |
|---|---|---|
| `Event` | `event` | Evento principal (físico o virtual) |
| `EventSpace` | `event_space` | Espacios/stands del evento |
| `EventInvitation` | `event_invitation` | Invitaciones a emprendimientos |
| `EventEntrepreneurshipParticipant` | `event_entrepreneurship_participant` | Participantes confirmados |

### Enumeraciones

| Enum | Valores |
|---|---|
| `EventType` | `PHYSICAL`, `VIRTUAL` |
| `EventVisibility` | `PUBLIC`, `PRIVATE` |
| `InvitationStatus` | `PENDING`, `ACCEPTED`, `REJECTED` |

---

## Reglas de Negocio

- Si `isPaid = false`, el campo `price` debe ser `NULL`
- Si `isPaid = true`, el campo `price` debe ser `>= 0`
- Un emprendimiento solo puede ser invitado una vez por evento
- `maxAttendees` controla la capacidad del público general
- `maxEntrepreneurships` controla la cantidad máxima de emprendimientos participantes
- Los eventos con `eventVisibilityId = PRIVATE` solo son visibles para invitados

---

## Endpoints REST

### Events

| Método | URL | Descripción | ⭐ Incluye Imagen |
|---|---|---|---|
| GET | `/api/v1/events` | Listar todos los eventos | ✅ |
| GET | `/api/v1/events/{id}` | Obtener evento por ID | ✅ |
| GET | `/api/v1/events/creator/{userId}` | Eventos de un creador | ✅ |
| GET | `/api/v1/events/search` | Búsqueda con filtros | ✅ |
| POST | `/api/v1/events` | Crear evento | ❌ |
| PUT | `/api/v1/events/{id}` | Actualizar evento | ❌ |
| DELETE | `/api/v1/events/{id}` | Eliminar evento (cascade) | ❌ |

### Event Spaces

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/event-spaces/event/{eventId}` | Listar espacios de un evento |
| GET | `/api/v1/event-spaces/{id}` | Obtener espacio por ID |
| POST | `/api/v1/event-spaces` | Crear nuevo espacio/stand |
| PUT | `/api/v1/event-spaces/{id}` | Actualizar espacio |
| DELETE | `/api/v1/event-spaces/{id}` | Eliminar espacio |

### Event Invitations

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/event-invitations/event/{eventId}` | Listar invitaciones de un evento |
| GET | `/api/v1/event-invitations/event/{eventId}?statusId={id}` | Filtrar por estado (PENDING/ACCEPTED/REJECTED) |
| GET | `/api/v1/event-invitations/{id}` | Obtener invitación por ID |
| POST | `/api/v1/event-invitations` | Enviar nueva invitación |
| PATCH | `/api/v1/event-invitations/{id}/status?statusId={id}` | Actualizar estado de invitación |
| DELETE | `/api/v1/event-invitations/{id}` | Eliminar invitación |

### Event Participants

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/event-participants/event/{eventId}` | Listar participantes confirmados |
| GET | `/api/v1/event-participants/event/{eventId}?statusId={id}` | Filtrar por estado (INVITED/ACCEPTED/REJECTED) |
| GET | `/api/v1/event-participants/{id}` | Obtener participante por ID |

### Parámetros de Búsqueda de Eventos

| Parámetro | Tipo | Descripción |
|---|---|---|
| `name` | String | Búsqueda parcial por nombre |
| `eventTypeId` | Long | Filtro por tipo (físico/virtual) |
| `eventVisibilityId` | Long | Filtro por visibilidad |
| `fromDate` | ISO DateTime | Filtro desde fecha |
| `toDate` | ISO DateTime | Filtro hasta fecha |

### Campos de EventResponse

```json
{
  "eventId": 1,
  "name": "Feria de Emprendedores 2026",
  "description": "Gran feria anual",
  "eventTypeId": 1,
  "isPaid": false,
  "startDatetime": "2026-06-01T10:00:00",
  "endDatetime": "2026-06-01T18:00:00",
  "imageUrl": "http://localhost:8084/api/files/events/1/cover.jpg",
  "imageId": 7,
  ...
}
```

**Nota**: 
- `imageUrl` y `imageId` se obtienen del **shared-service** (displayOrder=0)
- Si el evento no tiene portada, ambos campos son `null`

---

## Integración con Shared Service

### Propagación de Token JWT

El servicio utiliza `FeignClientConfiguration` para propagar automáticamente el token JWT a todas las llamadas al shared-service.

### Obtención de Imagen de Portada

En todos los endpoints GET de eventos, el servicio:
1. Obtiene los eventos de la BD
2. Llama a `shared-service` para obtener imágenes del evento
3. Busca la imagen con `displayOrder=0` (portada principal)
4. Enriquece el response con `imageUrl` e `imageId`

Si shared-service no responde, se usa Circuit Breaker y no se rompe el servicio.

---

## Estructura de Carpetas

```
event-service/src/main/java/com/project/emprendia/event/
├── client/
│   ├── SharedServiceClient.java           ← Comunicación con shared-service
│   ├── UserServiceClient.java
│   └── EntrepreneurshipServiceClient.java
├── configuration/
│   ├── FeignClientConfiguration.java      ← Propagación de JWT ⭐
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   └── KeycloakJwtAuthenticationConverter.java
├── controller/
│   └── EventController.java
├── domain/
│   ├── Event.java
│   ├── EventSpace.java
│   ├── EventInvitation.java
│   └── EventEntrepreneurshipParticipant.java
├── dto/
│   ├── EventRequest.java
│   ├── EventResponse.java                 ← Incluye imageUrl e imageId
│   ├── EventInvitationRequest.java
│   └── EventInvitationResponse.java
├── enums/
│   ├── EventType.java
│   ├── EventVisibility.java
│   └── InvitationStatus.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── DuplicateResourceException.java
├── mapping/mapper/
│   └── EventMapper.java
├── repository/
│   ├── EventRepository.java
│   ├── EventQueryRepository.java
│   ├── EventSpaceRepository.java
│   └── EventInvitationRepository.java
├── service/
│   ├── EventService.java
│   └── impl/
│       └── EventServiceImpl.java          ← Enriquecimiento con imágenes
└── util/
    └── DateUtil.java
```

---

## Flujo de Organización de Evento

### 1. Creación del Evento
```bash
POST /api/v1/events
```
Crea el evento con información básica (nombre, descripción, fechas, ubicación, etc.)

### 2. Subir Imagen de Portada
```bash
POST /api/images/upload (shared-service)
- entityType=EVENT
- entityId={eventId}
- displayOrder=0
```

### 3. Definir Espacios/Stands
```bash
POST /api/v1/event-spaces
{
  "eventId": 1,
  "spaceCode": "A-01",
  "isAvailable": true
}
```

### 4. Enviar Invitaciones a Emprendimientos
```bash
POST /api/v1/event-invitations
{
  "eventId": 1,
  "entrepreneurshipId": 5,
  "eventSpaceId": 1,
  "invitationStatusId": 1  // PENDING
}
```
El sistema valida:
- ✅ Que el evento existe
- ✅ Que el emprendimiento existe (llamada a entrepreneurship-service)
- ✅ Que no existe invitación duplicada

### 5. Consultar Invitaciones
```bash
# Todas las invitaciones del evento
GET /api/v1/event-invitations/event/1

# Solo pendientes
GET /api/v1/event-invitations/event/1?statusId=1

# Solo aceptadas
GET /api/v1/event-invitations/event/1?statusId=2

# Solo rechazadas
GET /api/v1/event-invitations/event/1?statusId=3
```

### 6. Emprendimiento Responde a la Invitación
```bash
PATCH /api/v1/event-invitations/1/status?statusId=2  # ACCEPTED
PATCH /api/v1/event-invitations/1/status?statusId=3  # REJECTED
```

### 7. Consultar Participantes Confirmados
```bash
GET /api/v1/event-participants/event/1
GET /api/v1/event-participants/event/1?statusId=2  # ACCEPTED
```

---

## Estados de Invitaciones y Participación

### Invitation Status (INVITATION_STATUS)
| ID | Código | Nombre | Descripción |
|---|---|---|---|
| Variable | PENDING | Pending | Invitación enviada, esperando respuesta |
| Variable | ACCEPTED | Accepted | Invitación aceptada por el emprendimiento |
| Variable | REJECTED | Rejected | Invitación rechazada |

### Participation Status (EVENT_PARTICIPATION_STATUS)
| ID | Código | Nombre | Descripción |
|---|---|---|---|
| Variable | INVITED | Invited | Emprendimiento invitado al evento |
| Variable | ACCEPTED | Accepted | Emprendimiento confirmó su participación |
| Variable | REJECTED | Rejected | Emprendimiento declinó participar |

**Nota**: Los IDs son dinámicos y se obtienen del catálogo en shared-service.

---

## Eliminación en Cascada

Al eliminar un evento (`DELETE /api/v1/events/{id}`), se eliminan automáticamente:
- ✅ Todos los espacios (`EventSpace`)
- ✅ Todas las invitaciones (`EventInvitation`)  
- ✅ Todos los registros de participantes (`EventEntrepreneurshipParticipant`)

Esto se logra mediante `CascadeType.ALL` en las relaciones `@OneToMany`.

---

## Changelog

### v1.1.0 - 29 Mayo 2026
- ✅ Agregada integración con shared-service para imágenes de portada
- ✅ Creado `FeignClientConfiguration` para propagación automática de JWT
- ✅ Agregados campos `imageUrl` e `imageId` en `EventResponse`
- ✅ Implementado enriquecimiento automático en todos los endpoints GET
