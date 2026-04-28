# Event Service - Dominio de Eventos

## Descripción

Microservicio que gestiona eventos (ferias, exposiciones), espacios disponibles, invitaciones a emprendimientos y el seguimiento de participación en los eventos.

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

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/events` | Listar todos los eventos |
| GET | `/api/v1/events/{id}` | Obtener evento por ID |
| GET | `/api/v1/events/creator/{userId}` | Eventos de un creador |
| GET | `/api/v1/events/search` | Búsqueda con filtros |
| POST | `/api/v1/events` | Crear evento |
| PUT | `/api/v1/events/{id}` | Actualizar evento |
| DELETE | `/api/v1/events/{id}` | Eliminar evento |

### Parámetros de Búsqueda de Eventos

| Parámetro | Tipo | Descripción |
|---|---|---|
| `name` | String | Búsqueda parcial por nombre |
| `eventTypeId` | Long | Filtro por tipo (físico/virtual) |
| `eventVisibilityId` | Long | Filtro por visibilidad |
| `fromDate` | ISO DateTime | Filtro desde fecha |
| `toDate` | ISO DateTime | Filtro hasta fecha |

---

## Estructura de Carpetas

```
event-service/src/main/java/com/project/emprendia/event/
├── configuration/
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
│   ├── EventResponse.java
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
│       └── EventServiceImpl.java
└── util/
    └── DateUtil.java
```

---

## Flujo de Organización de Evento

1. Creador llama `POST /api/v1/events` → crea el evento
2. Define espacios disponibles en `event_space`
3. Envía invitaciones a emprendimientos via `event_invitation`
4. Los emprendimientos responden (ACCEPTED / REJECTED)
5. Los confirmados se registran en `event_entrepreneurship_participant`
