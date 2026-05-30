# Guía de API - Emprendia
**Versión**: 3.0 | **Fecha**: Mayo 8, 2026

---

## 📋 Contenido

1. [Colecciones Postman](#colecciones-postman)
2. [Configuración](#configuración)
3. [Endpoints por Servicio](#endpoints-por-servicio)
4. [Ejemplos de Uso](#ejemplos-de-uso)
5. [Gestión de Imágenes](#gestión-de-imágenes)

---

## 📮 Colecciones Postman

Ubicación: `postman/`

| Archivo | Descripción |
|---------|-------------|
| `Emprendia.postman_environment.json` | Variables de entorno |
| `00-Auth-Keycloak.postman_collection.json` | Autenticación OAuth2 |
| `01-shared-service.postman_collection.json` | Catálogos + imágenes |
| `02-user-service.postman_collection.json` | Gestión de usuarios |
| `03-entrepreneurship-service.postman_collection.json` | Emprendimientos |
| `04-event-service.postman_collection.json` | Eventos |
| `05-image-management.postman_collection.json` | Gestión completa de imágenes ⭐ |

### Importar Colecciones

1. Abrir Postman → File → Import
2. Arrastrar todos los `.json` de `postman/`
3. Seleccionar environment "Emprendia" en Postman

### Configurar Environment

Editar variables en Postman:

| Variable | Valor |
|----------|-------|
| `keycloak_url` | `http://localhost:8080` |
| `keycloak_realm` | `emprendia` |
| `keycloak_client_id` | `emprendia-app` |
| `keycloak_username` | `tu_usuario` |
| `keycloak_password` | `tu_password` |
| `shared_url` | `http://localhost:8084` |
| `user_url` | `http://localhost:8081` |
| `entrepreneurship_url` | `http://localhost:8082` |
| `event_url` | `http://localhost:8083` |
| `access_token` | *(auto)* |

---

## 🔑 Autenticación

### Flujo

```
1. Ejecutar: 00-Auth > Login
   ↓
2. Se guarda automáticamente: {{access_token}}
   ↓
3. Usar cualquier colección (token se envía automáticamente)
```

### Endpoints Públicos (sin token)

- `GET /api/v1/catalogue-types/**`
- `GET /api/v1/catalogue-values/**`
- `GET /api/images` (lectura)
- `GET /api/images/count`
- `GET /api/images/can-add-more`
- `GET /api/files/**`
- `GET /actuator/health`

### Endpoints Protegidos

Todos los demás requieren `Authorization: Bearer {{access_token}}`

---

## 📡 Endpoints por Servicio

### 1. Shared Service (8084)

#### Catalogue Types

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/catalogue-types` | Listar todos | Público |
| GET | `/api/v1/catalogue-types/{id}` | Por ID | Público |
| POST | `/api/v1/catalogue-types` | Crear | ADMIN |
| PUT | `/api/v1/catalogue-types/{id}` | Actualizar | ADMIN |
| DELETE | `/api/v1/catalogue-types/{id}` | Eliminar | ADMIN |

#### Catalogue Values

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/catalogue-values/by-type/{typeCode}` | Por tipo | Público |
| GET | `/api/v1/catalogue-values/{id}` | Por ID | Público |
| GET | `/api/v1/catalogue-values/{id}/children` | Hijos | Público |
| POST | `/api/v1/catalogue-values` | Crear | ADMIN |
| PUT | `/api/v1/catalogue-values/{id}` | Actualizar | ADMIN |
| DELETE | `/api/v1/catalogue-values/{id}` | Eliminar | ADMIN |

#### Image Gallery

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/images/upload` | Subir imagen | Bearer |
| GET | `/api/images?entityType=X&entityId=Y` | Listar | Público |
| GET | `/api/images/{id}` | Por ID | Público |
| PUT | `/api/images/{id}` | Actualizar metadata | Bearer |
| DELETE | `/api/images/{id}` | Eliminar | Bearer |
| DELETE | `/api/images?entityType=X&entityId=Y` | Eliminar todas | Bearer |
| POST | `/api/images/reorder` | Reordenar | Bearer |
| GET | `/api/images/count` | Contar | Público |
| GET | `/api/images/can-add-more` | Verificar límite | Público |
| GET | `/api/files/**` | Servir archivo (local) | Público |

---

### 2. User Service (8081)

#### Users

| Método | Endpoint | Descripción | ⭐ Incluye Imagen |
|--------|----------|-------------|------------------|
| GET | `/api/v1/users` | Listar todos | ✅ |
| GET | `/api/v1/users/{id}` | Por ID | ✅ |
| GET | `/api/v1/users/keycloak/{keycloakId}` | Por Keycloak ID | ✅ |
| POST | `/api/v1/users` | Crear | ❌ |
| PUT | `/api/v1/users/{id}` | Actualizar | ❌ |
| DELETE | `/api/v1/users/{id}` | Eliminar | ❌ |

**Campos de Respuesta con Imagen**:
```json
{
  "userId": 1,
  "keycloakId": "uuid-123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "imageUrl": "http://localhost:8084/api/files/users/1/profile.jpg",
  "imageId": 3
}
```

#### User Contacts

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/user-contacts` | Listar |
| GET | `/api/v1/user-contacts/{id}` | Por ID |
| POST | `/api/v1/user-contacts` | Crear |
| PUT | `/api/v1/user-contacts/{id}` | Actualizar |
| DELETE | `/api/v1/user-contacts/{id}` | Eliminar |

#### User Addresses

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/user-addresses` | Listar |
| GET | `/api/v1/user-addresses/{id}` | Por ID |
| POST | `/api/v1/user-addresses` | Crear |
| PUT | `/api/v1/user-addresses/{id}` | Actualizar |
| DELETE | `/api/v1/user-addresses/{id}` | Eliminar |

#### User Identifications

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/user-identifications` | Listar |
| GET | `/api/v1/user-identifications/{id}` | Por ID |
| POST | `/api/v1/user-identifications` | Crear |
| PUT | `/api/v1/user-identifications/{id}` | Actualizar |
| DELETE | `/api/v1/user-identifications/{id}` | Eliminar |

---

### 3. Entrepreneurship Service (8082)

#### Entrepreneurships

| Método | Endpoint | Descripción | ⭐ Incluye Imagen | 🔢 Paginado |
|--------|----------|-------------|------------------|-------------|
| GET | `/api/v1/entrepreneurships` | Listar todos | ❌ | ❌ |
| GET | `/api/v1/entrepreneurships/{id}` | Por ID | ❌ | ❌ |
| GET | `/api/v1/entrepreneurships/user/{userId}` | Por usuario | ✅ | ❌ |
| GET | `/api/v1/entrepreneurships/search` | Búsqueda con filtros | ✅ | ✅ Opcional |
| POST | `/api/v1/entrepreneurships` | Crear | ❌ | ❌ |
| PUT | `/api/v1/entrepreneurships/{id}` | Actualizar | ❌ | ❌ |
| DELETE | `/api/v1/entrepreneurships/{id}` | Eliminar | ❌ | ❌ |

**Filtros de búsqueda:**
- `name` (String) - Búsqueda parcial por nombre
- `categoryId` (Long) - Filtro por categoría
- `isPhysical` (Boolean) - Filtro por presencia física
- `isDigital` (Boolean) - Filtro por presencia digital
- `page` (Integer) - Número de página (opcional, para paginación)
- `size` (Integer) - Tamaño de página (opcional, para paginación)

**Ejemplos**:
```bash
# Sin paginación (retorna List<>)
GET /api/v1/entrepreneurships/search?name=panaderia&categoryId=1

# Con paginación (retorna Page<>)
GET /api/v1/entrepreneurships/search?name=panaderia&page=0&size=10
```

**Campos de Respuesta con Imagen**:
```json
{
  "entrepreneurshipId": 1,
  "name": "Panadería Artesanal",
  "categoryName": "Alimentos",
  "imageUrl": "http://localhost:8084/api/files/entrepreneurships/1/logo.jpg",
  "imageId": 5
}
```

#### Categories

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/categories` | Listar |
| GET | `/api/v1/categories/{id}` | Por ID |
| GET | `/api/v1/categories/name/{name}` | Por nombre |
| POST | `/api/v1/categories` | Crear |
| PUT | `/api/v1/categories/{id}` | Actualizar |
| DELETE | `/api/v1/categories/{id}` | Eliminar |

---

### 4. Event Service (8083)

#### Events

| Método | Endpoint | Descripción | ⭐ Incluye Imagen |
|--------|----------|-------------|------------------|
| GET | `/api/v1/events` | Listar todos | ✅ |
| GET | `/api/v1/events/{id}` | Por ID | ✅ |
| GET | `/api/v1/events/creator/{userId}` | Por creador | ✅ |
| GET | `/api/v1/events/search` | Búsqueda con filtros | ✅ |
| POST | `/api/v1/events` | Crear | ❌ |
| PUT | `/api/v1/events/{id}` | Actualizar | ❌ |
| DELETE | `/api/v1/events/{id}` | Eliminar | ❌ |

**Filtros de búsqueda:**
- `name` (String)
- `eventTypeId` (Long)
- `eventVisibilityId` (Long)
- `fromDate` (LocalDateTime)
- `toDate` (LocalDateTime)

**Campos de Respuesta con Imagen**:
```json
{
  "eventId": 1,
  "name": "Feria de Emprendedores 2026",
  "description": "Gran feria anual",
  "imageUrl": "http://localhost:8084/api/files/events/1/cover.jpg",
  "imageId": 7
}
```

#### Event Spaces

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/events/{eventId}/spaces` | Listar espacios |
| POST | `/api/v1/event-spaces` | Crear espacio |

#### Event Invitations

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/events/{eventId}/invitations` | Listar invitaciones |
| POST | `/api/v1/event-invitations` | Crear invitación |

---

## 📸 Gestión de Imágenes

### Límites

| Entidad | Límite |
|---------|--------|
| USER | 5 imágenes |
| ENTREPRENEURSHIP | 10 imágenes |
| EVENT | 20 imágenes |

### Workflow Completo

```
1. Verificar límite
   GET /api/images/can-add-more?entityType=USER&entityId=1
   Response: true

2. Subir imagen
   POST /api/images/upload
   Form-data:
   - file: (archivo)
   - entityType: USER
   - entityId: 1
   - displayOrder: 0
   - altText: "Foto de perfil"

3. Listar imágenes
   GET /api/images?entityType=USER&entityId=1

4. Reordenar
   POST /api/images/reorder?entityType=USER&entityId=1
   Body: [3, 1, 2]  (nuevo orden de IDs)
```

### Ejemplo Upload (cURL)

```bash
curl -X POST http://localhost:8084/api/images/upload \
  -H "Authorization: Bearer TOKEN" \
  -F "file=@profile.jpg" \
  -F "entityType=USER" \
  -F "entityId=1" \
  -F "displayOrder=0" \
  -F "altText=Foto de perfil"
```

### Ejemplo Upload (Postman)

1. Abrir `05-image-management`
2. Seleccionar `Upload User Profile Image`
3. En Body → form-data → file → Select Files
4. Ajustar `entityId` si es necesario
5. Send

### Validaciones

- **Max size**: 5 MB
- **Formatos**: JPEG, PNG, WebP, GIF
- **Campos requeridos**: file, entityType, entityId
- **Campos opcionales**: displayOrder, altText, description, uploadedByUserId

---

## 💡 Ejemplos de Uso

### 1. Crear Usuario Completo

```bash
# 1. Login
POST {{keycloak_url}}/realms/{{realm}}/protocol/openid-connect/token
Body: grant_type=password&client_id=emprendia-app&username=admin&password=admin

# 2. Crear usuario
POST {{user_url}}/api/v1/users
Body: {
  "keycloakId": "uuid-123",
  "firstName": "Juan",
  "lastName": "Pérez"
}

# 3. Agregar contacto
POST {{user_url}}/api/v1/user-contacts
Body: {
  "userId": 1,
  "contactTypeId": 1,
  "contactValue": "0987654321",
  "isPrimary": true
}

# 4. Subir foto
POST {{shared_url}}/api/images/upload
Form-data: file, entityType=USER, entityId=1
```

### 2. Crear Emprendimiento con Galería

```bash
# 1. Crear emprendimiento
POST {{entrepreneurship_url}}/api/v1/entrepreneurships
Body: {
  "userId": 1,
  "categoryId": 1,
  "name": "Mi Emprendimiento",
  "description": "Descripción",
  "isPhysical": true,
  "isDigital": false
}

# 2. Verificar límite
GET {{shared_url}}/api/images/can-add-more?entityType=ENTREPRENEURSHIP&entityId=1
Response: true

# 3. Subir logo (displayOrder=0)
POST {{shared_url}}/api/images/upload
Form-data: file, entityType=ENTREPRENEURSHIP, entityId=1, displayOrder=0

# 4. Subir productos (displayOrder>0)
POST {{shared_url}}/api/images/upload
Form-data: file, entityType=ENTREPRENEURSHIP, entityId=1, displayOrder=1

# 5. Listar galería
GET {{shared_url}}/api/images?entityType=ENTREPRENEURSHIP&entityId=1
```

### 3. Crear Evento con Invitaciones

```bash
# 1. Crear evento
POST {{event_url}}/api/v1/events
Body: {
  "createdByUserId": 1,
  "name": "Feria de Emprendedores 2026",
  "description": "Gran feria",
  "eventTypeId": 1,
  "eventVisibilityId": 1,
  "isPaid": false,
  "startDatetime": "2026-06-01T10:00:00",
  "endDatetime": "2026-06-01T18:00:00",
  "countryId": 1,
  "provinceId": 1,
  "cityId": 1,
  "addressLine": "Centro de Convenciones"
}
Response: { "eventId": 1 }

# 2. Subir imagen de portada
POST {{shared_url}}/api/images/upload
Form-data: file, entityType=EVENT, entityId=1, displayOrder=0

# 3. Crear espacios/stands
POST {{event_url}}/api/v1/event-spaces
Body: {
  "eventId": 1,
  "spaceCode": "A-01",
  "isAvailable": true
}

POST {{event_url}}/api/v1/event-spaces
Body: {
  "eventId": 1,
  "spaceCode": "A-02",
  "isAvailable": true
}

# 4. Invitar emprendimientos (estado PENDING)
POST {{event_url}}/api/v1/event-invitations
Body: {
  "eventId": 1,
  "entrepreneurshipId": 1,
  "eventSpaceId": 1,
  "invitationStatusId": 1
}

# 5. Consultar invitaciones pendientes
GET {{event_url}}/api/v1/event-invitations/event/1?statusId=1

# 6. Emprendimiento acepta invitación
PATCH {{event_url}}/api/v1/event-invitations/1/status?statusId=2

# 7. Consultar participantes confirmados
GET {{event_url}}/api/v1/event-participants/event/1?statusId=2
```

### 4. Gestionar Información de Emprendimiento

```bash
# 1. Agregar ubicación física
POST {{entrepreneurship_url}}/api/v1/entrepreneurship-locations
Body: {
  "entrepreneurshipId": 1,
  "countryId": 1,
  "provinceId": 2,
  "cityId": 15,
  "parishId": 100,
  "addressLine": "Av. Principal 123",
  "latitude": -0.123456,
  "longitude": -78.654321
}

# 2. Agregar redes sociales
POST {{entrepreneurship_url}}/api/v1/entrepreneurship-social-links
Body: {
  "entrepreneurshipId": 1,
  "socialPlatformId": 1,
  "url": "https://facebook.com/mi.emprendimiento"
}

POST {{entrepreneurship_url}}/api/v1/entrepreneurship-social-links
Body: {
  "entrepreneurshipId": 1,
  "socialPlatformId": 2,
  "url": "https://instagram.com/mi_emprendimiento"
}

# 3. Crear portal web
POST {{entrepreneurship_url}}/api/v1/entrepreneurship-portals
Body: {
  "entrepreneurshipId": 1,
  "subdomain": "mi-emprendimiento",
  "themeId": 1,
  "isActive": true
}

# 4. Consultar toda la información
GET {{entrepreneurship_url}}/api/v1/entrepreneurships/1
GET {{entrepreneurship_url}}/api/v1/entrepreneurship-locations/entrepreneurship/1
GET {{entrepreneurship_url}}/api/v1/entrepreneurship-social-links/entrepreneurship/1
GET {{entrepreneurship_url}}/api/v1/entrepreneurship-portals/entrepreneurship/1
GET {{shared_url}}/api/images?entityType=ENTREPRENEURSHIP&entityId=1
```

---

## 🐛 Troubleshooting

### Error 401 Unauthorized

**Solución:**
1. Ejecutar `00-Auth > Login`
2. Verificar que `{{access_token}}` tiene valor
3. Token expira en ~5 min, renovar si es necesario

### Error 404 Not Found

**Solución:**
1. Verificar que el servicio está corriendo
2. Confirmar el puerto correcto
3. Verificar que el ID del recurso existe

### Error 400 Bad Request en Upload

**Solución:**
1. Verificar formato de imagen (JPEG, PNG, WebP, GIF)
2. Confirmar tamaño < 5MB
3. Ejecutar `/can-add-more` para verificar límite

### Error 403 Forbidden

**Solución:**
1. Algunos endpoints requieren `ROLE_ADMIN`
2. Verificar roles en Keycloak

### Variables no se cargan

**Solución:**
1. Seleccionar environment "Emprendia" en Postman (esquina superior derecha)
2. Verificar que las variables están definidas

---

## 📝 Variables Dinámicas

Las colecciones guardan automáticamente:

- `access_token` - JWT del login
- `last_user_id` - Último usuario creado
- `last_entrepreneurship_id` - Último emprendimiento creado
- `last_event_id` - Último evento creado
- `last_image_id` - Última imagen subida
- `user_profile_image_id` - Imagen de perfil
- `entrepreneurship_logo_id` - Logo
- `event_cover_id` - Portada de evento

Usarlas: `{{last_user_id}}`

---

## 🧪 Testing Automatizado

Todas las requests incluyen tests:

```javascript
pm.test('Status 200 OK', () => {
    pm.response.to.have.status(200);
});

pm.test('Response es array', () => {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test('Tiene imageId', () => {
    const r = pm.response.json();
    pm.expect(r).to.have.property('imageId');
});
```

Ejecutar colección completa: Collection → Run

---

## 🎯 Orden Recomendado de Prueba

```
1. 00-Auth > Login
   ↓
2. 01-Shared > GET Catalogue Types
   ↓
3. 01-Shared > GET Catalogue Values by Type (COUNTRY, CATEGORY)
   ↓
4. 02-User > POST Create User
   ↓
5. 02-User > POST Create Contact
   ↓
6. 05-Image Management > Upload User Profile Image
   ↓
7. 03-Entrepreneurship > POST Create Entrepreneurship
   ↓
8. 05-Image Management > Upload Entrepreneurship Logo
   ↓
9. 04-Event > POST Create Event
   ↓
10. 05-Image Management > Upload Event Cover
```

---

## 🆕 Changelog - v3.2 (29 Mayo 2026)

### ✨ Nuevas Funcionalidades

#### 1. Gestión Completa de Invitaciones a Eventos

**Event Service** ahora incluye flujo completo de invitaciones:
- ✅ POST `/api/v1/event-invitations` - Enviar invitación a emprendimiento
- ✅ GET `/api/v1/event-invitations/event/{eventId}` - Listar invitaciones
- ✅ GET con filtro `?statusId={id}` - Filtrar por PENDING/ACCEPTED/REJECTED
- ✅ PATCH `/api/v1/event-invitations/{id}/status` - Actualizar estado
- ✅ DELETE `/api/v1/event-invitations/{id}` - Eliminar invitación
- ✅ Validación automática de existencia de emprendimiento (llamada a entrepreneurship-service)
- ✅ Validación de invitaciones duplicadas

#### 2. CRUD de Espacios de Evento

- ✅ GET `/api/v1/event-spaces/event/{eventId}` - Listar espacios/stands
- ✅ POST `/api/v1/event-spaces` - Crear espacio
- ✅ PUT `/api/v1/event-spaces/{id}` - Actualizar espacio
- ✅ DELETE `/api/v1/event-spaces/{id}` - Eliminar espacio

#### 3. Consulta de Participantes Confirmados

- ✅ GET `/api/v1/event-participants/event/{eventId}` - Listar participantes
- ✅ Filtro por estado de participación
- ✅ Incluye información de evento y emprendimiento

#### 4. CRUD de Ubicaciones de Emprendimiento

**Entrepreneurship Service** ahora gestiona ubicaciones físicas:
- ✅ POST `/api/v1/entrepreneurship-locations` - Crear ubicación con GPS
- ✅ GET `/api/v1/entrepreneurship-locations/entrepreneurship/{id}` - Listar ubicaciones
- ✅ PUT `/api/v1/entrepreneurship-locations/{id}` - Actualizar ubicación
- ✅ DELETE `/api/v1/entrepreneurship-locations/{id}` - Eliminar ubicación

#### 5. CRUD de Redes Sociales

- ✅ POST `/api/v1/entrepreneurship-social-links` - Agregar red social
- ✅ GET `/api/v1/entrepreneurship-social-links/entrepreneurship/{id}` - Listar redes
- ✅ PUT `/api/v1/entrepreneurship-social-links/{id}` - Actualizar red social
- ✅ DELETE `/api/v1/entrepreneurship-social-links/{id}` - Eliminar red social

#### 6. CRUD de Portales Web Personalizados

- ✅ POST `/api/v1/entrepreneurship-portals` - Crear portal con subdomain único
- ✅ GET `/api/v1/entrepreneurship-portals/entrepreneurship/{id}` - Obtener portal
- ✅ PUT `/api/v1/entrepreneurship-portals/{id}` - Actualizar portal
- ✅ DELETE `/api/v1/entrepreneurship-portals/{id}` - Eliminar portal
- ✅ Validación de subdomain único en toda la plataforma

### 🔧 Mejoras Técnicas

#### Eliminación en Cascada

**Event Service**:
- ✅ Al eliminar un evento, se eliminan automáticamente espacios, invitaciones y participantes

**Entrepreneurship Service**:
- ✅ Al eliminar un emprendimiento, se eliminan automáticamente ubicaciones, redes sociales y portal

#### Estados de Invitación

Los estados se gestionan mediante catálogos en shared-service:
- **INVITATION_STATUS**: PENDING (1), ACCEPTED (2), REJECTED (3)
- **EVENT_PARTICIPATION_STATUS**: INVITED (1), ACCEPTED (2), REJECTED (3)

#### Documentación Swagger/OpenAPI

- ✅ Todos los nuevos endpoints documentados con @Tag, @Operation, @Parameter
- ✅ Swagger UI disponible en:
  - Event Service: http://localhost:8083/swagger-ui.html
  - Entrepreneurship Service: http://localhost:8082/swagger-ui.html

---

## 🆕 Changelog - v3.1 (29 Mayo 2026)

### ✨ Nuevas Funcionalidades

#### 1. Integración Automática de Imágenes

Los servicios **user-service** y **entrepreneurship-service** ahora incluyen automáticamente las imágenes en sus respuestas:

**User Service**:
- ✅ GET `/api/v1/users` - Incluye `imageUrl` e `imageId`
- ✅ GET `/api/v1/users/{id}` - Incluye foto de perfil
- ✅ GET `/api/v1/users/keycloak/{keycloakId}` - Incluye foto de perfil

**Entrepreneurship Service**:
- ✅ GET `/api/v1/entrepreneurships/user/{userId}` - Incluye logo
- ✅ GET `/api/v1/entrepreneurships/search` - Incluye logo

**Event Service**:
- ✅ GET `/api/v1/events` - Incluye portada
- ✅ GET `/api/v1/events/{id}` - Incluye portada
- ✅ GET `/api/v1/events/creator/{userId}` - Incluye portada
- ✅ GET `/api/v1/events/search` - Incluye portada

#### 2. Paginación en Búsqueda de Emprendimientos

El endpoint `/search` ahora soporta paginación opcional:

```bash
# Sin paginación (comportamiento anterior - compatible)
GET /api/v1/entrepreneurships/search?name=panaderia
→ Retorna: List<EntrepreneurshipResponse>

# Con paginación (nuevo)
GET /api/v1/entrepreneurships/search?name=panaderia&page=0&size=10
→ Retorna: Page<EntrepreneurshipResponse> con metadatos
```

### 🔧 Mejoras Técnicas

#### Propagación Automática de JWT

Se implementó `FeignClientConfiguration` en los tres servicios (user, entrepreneurship, event) para:
- ✅ Propagar automáticamente el token JWT a llamadas entre microservicios
- ✅ Solucionar problema de autenticación 401 en llamadas a shared-service
- ✅ Usar Circuit Breaker para resiliencia

#### Comportamiento de Fallback

Si shared-service no responde:
- ✅ No se rompe el servicio principal
- ✅ `imageUrl` e `imageId` serán `null`
- ✅ El frontend puede mostrar imagen por defecto

### 📊 Respuestas con Imágenes

**UserResponse**:
```json
{
  "userId": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "imageUrl": "http://localhost:8084/api/files/users/1/profile.jpg",
  "imageId": 3
}
```

**EntrepreneurshipResponse**:
```json
{
  "entrepreneurshipId": 1,
  "name": "Panadería Artesanal",
  "imageUrl": "http://localhost:8084/api/files/entrepreneurships/1/logo.jpg",
  "imageId": 5
}
```

**EventResponse**:
```json
{
  "eventId": 1,
  "name": "Feria de Emprendedores 2026",
  "imageUrl": "http://localhost:8084/api/files/events/1/cover.jpg",
  "imageId": 7
}
```

**Nota**: Las imágenes con `displayOrder=0` son las principales (foto de perfil / logo / portada).

---

**Autor**: Kevin Guachagmira  
**Email**: mantillagka@gmail.com  
**Proyecto**: Plataforma Emprendia - Tesis NIBE  
**Versión**: 3.1 | Mayo 29, 2026

