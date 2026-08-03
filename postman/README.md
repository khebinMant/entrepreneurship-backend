# Colecciones Postman - Emprendia

Colecciones de prueba para los microservicios de la plataforma Emprendia.

## 📋 Colecciones Incluidas

### 00 – Auth Keycloak
Autenticación OAuth2 con Keycloak:
- Login (obtener access token)
- Refresh token
- Introspect token
- Logout

### 01 – Shared Service (Puerto 8084)
Servicios compartidos:
- **Catalogue Types**: Tipos de catálogo (CATEGORY, COUNTRY, etc.)
- **Catalogue Values**: Valores de catálogo jerárquicos
- **Image Gallery**: Sistema de gestión de imágenes
  - Upload multipart con validaciones (max 5MB, límites por entidad)
  - CRUD completo de imágenes
  - Reordenamiento y contadores
  - File serving para storage local

### 02 – User Service (Puerto 8081)
Gestión de usuarios:
- **Users**: CRUD de usuarios con keycloakId
- **User Contacts**: Teléfonos, emails, etc.
- **User Addresses**: Direcciones con jerarquía geográfica
- **User Identifications**: Cédula, RUC, pasaporte

### 03 – Entrepreneurship Service (Puerto 8082)
Gestión de emprendimientos:
- **Entrepreneurships**: CRUD completo con filtros de búsqueda
- **Categories**: Categorías de emprendimientos
- Búsqueda por userId, categoryId, físico/digital

### 04 – Event Service (Puerto 8083)
Gestión de eventos:
- **Events**: CRUD de eventos físicos/virtuales, públicos/privados, gratuitos/pagos
- **Event Spaces**: Espacios dentro de eventos
- **Event Invitations**: Invitaciones a emprendimientos
- Búsqueda por fechas, tipo, visibilidad

### 05 – Image Management ⭐ NUEVA
Gestión completa de imágenes para todas las entidades:
- **USER Images**: Profile + galería (certificados, premios) - Límite: 5
- **ENTREPRENEURSHIP Images**: Logo + galería de productos - Límite: 10
- **EVENT Images**: Portada + galería de momentos - Límite: 20
- Upload multipart con validaciones automáticas
- Reordenamiento de galería (drag & drop simulado)
- Verificación de límites antes de subir
- Descarga directa de archivos (solo storage local)

## 🚀 Configuración Inicial

### 1. Importar Environment

Importa uno de los dos environments disponibles:

**`Emprendia.postman_environment.json` (Local)** — apunta al **gateway local**:
```
keycloak_url: http://localhost:8080
keycloak_realm: emprendia
keycloak_client_id: emprendia-app
keycloak_username: admin
keycloak_password: admin

user_url: http://localhost:8090/api/user
entrepreneurship_url: http://localhost:8090/api/entrepreneurship
event_url: http://localhost:8090/api/event
shared_url: http://localhost:8090/api/shared
gateway_url: http://localhost:8090
```

**`Emprendia-Prod.postman_environment.json` (Producción)** — apunta al dominio público (same esquema de rutas):
```
keycloak_url: https://emprendia.duckdns.org
user_url: https://emprendia.duckdns.org/api/user
entrepreneurship_url: https://emprendia.duckdns.org/api/entrepreneurship
event_url: https://emprendia.duckdns.org/api/event
shared_url: https://emprendia.duckdns.org/api/shared
gateway_url: https://emprendia.duckdns.org
```

> **Esquema de rutas vía gateway**: todas las peticiones entran por el gateway (`:8090` local / `emprendia.duckdns.org` en prod), que enruta a cada microservicio con el prefijo de servicio. Las URLs de archivos (`/api/files/**`) usan `gateway_url` sin prefijo de servicio.

**IMPORTANTE**: Actualiza `keycloak_username` y `keycloak_password` con tus credenciales reales.

### 2. Obtener Access Token

#### Opción A: Automático
Las colecciones tienen un pre-request script que obtiene el token automáticamente si está vacío. Solo ejecuta cualquier endpoint.

#### Opción B: Manual
1. Ejecuta `00-Auth-Keycloak > Login`
2. El token se guardará automáticamente en `{{access_token}}`
3. Las demás colecciones lo usarán en el header `Authorization: Bearer`

## 📸 Sistema de Imágenes

### Límites por Entidad
- **USER**: 5 imágenes
- **ENTREPRENEURSHIP**: 10 imágenes
- **EVENT**: 20 imágenes

### Estructura de rutas (storage local)
```
users/{userId}/profile.jpg
users/{userId}/gallery/{timestamp}_{uuid}.jpg
entrepreneurships/{id}/logo.jpg
entrepreneurships/{id}/gallery/{timestamp}_{uuid}.jpg
events/{id}/cover.jpg
events/{id}/gallery/{timestamp}_{uuid}.jpg
```

### Workflow de Subida
1. **Verificar límite**: `GET /api/images/can-add-more?entityType=USER&entityId=1`
2. **Subir imagen**: `POST /api/images/upload` (form-data con file + metadata)
3. **Listar imágenes**: `GET /api/images?entityType=USER&entityId=1`
4. **Reordenar**: `POST /api/images/reorder` con array de IDs

### Validaciones
- Max 5MB por archivo
- Formatos: JPEG, PNG, WebP, GIF
- Dimensiones extraídas automáticamente
- MIME type validado

## 🔑 Autenticación

### Endpoints Públicos (NO requieren token)
- `GET /api/v1/catalogue-types/**`
- `GET /api/v1/catalogue-values/**`
- `GET /api/images` (lectura)
- `GET /api/images/count`
- `GET /api/images/can-add-more`
- `GET /api/files/**` (file serving)
- `GET /actuator/health` (todos los servicios)

### Endpoints Protegidos
Todos los demás requieren `Authorization: Bearer {{access_token}}`.

## 🧪 Tests Automatizados

Todas las requests incluyen tests de validación:
- ✅ Status code esperado (200, 201, 204, 404)
- ✅ Tipo de respuesta (array, object, number, boolean)
- ✅ Propiedades requeridas (id, imageUrl, etc.)
- ✅ Guardado automático de IDs en variables de entorno

## 📊 Orden de Ejecución Recomendado

### Setup Inicial
1. `00-Auth > Login` → obtener token
2. `01-Shared > Catalogue Types > GET Listar todos` → ver catálogos disponibles
3. `01-Shared > Catalogue Values > GET Por tipo` → obtener valores (COUNTRY, CATEGORY, etc.)

### Crear Usuario Completo
1. `02-User > Users > POST Crear usuario`
2. `02-User > User Contacts > POST Crear contacto` (teléfono)
3. `02-User > User Contacts > POST Crear contacto` (email)
4. `02-User > User Addresses > POST Crear dirección`
5. `02-User > User Identifications > POST Crear identificación`
6. `01-Shared > Image Gallery > Upload Image` (entityType=USER, entityId={userId})

### Crear Emprendimiento Completo
1. `03-Entrepreneurship > Entrepreneurships > POST Crear emprendimiento`
2. `05-Image Management > ENTREPRENEURSHIP Images > Upload Logo` (displayOrder=0)
3. `05-Image Management > ENTREPRENEURSHIP Images > Upload Product Image` (galería)
4. `05-Image Management > ENTREPRENEURSHIP Images > Reorder Product Images`

### Crear Evento
1. `04-Event > Events > POST Crear evento`
2. `04-Event > Event Spaces > POST Crear espacio`
3. `04-Event > Event Invitations > POST Crear invitación`
4. `05-Image Management > EVENT Images > Upload Event Cover` (displayOrder=0)
5. `05-Image Management > EVENT Images > Upload Event Gallery Image`

### Gestión de Imágenes Completa
1. **Verificar límite**: `05-Image Management > Can Add More Images`
2. **Subir imagen**: `05-Image Management > Upload Image` (según tipo de entidad)
3. **Listar imágenes**: `05-Image Management > Get Images`
4. **Reordenar**: `05-Image Management > Reorder Images`
5. **Actualizar metadata**: `05-Image Management > Update Image Metadata`
6. **Eliminar**: `05-Image Management > Delete Image`

## 🐛 Troubleshooting

### Error 401 Unauthorized
- ✅ Verifica que `{{access_token}}` tenga valor
- ✅ Ejecuta `00-Auth > Login` si expiró (tokens duran ~5 minutos)
- ✅ Revisa que Keycloak esté corriendo en puerto 8080

### Error 404 Not Found
- ✅ Verifica que el gateway esté corriendo (puerto 8090) o el dominio público esté arriba
- ✅ Revisa que los microservicios estén ejecutándose
- ✅ Confirma que el ID del recurso exista

### Error 400 Bad Request en Upload
- ✅ Verifica que el archivo sea < 5MB
- ✅ Confirma formato JPEG/PNG/WebP/GIF
- ✅ Usa `GET /can-add-more` para verificar límites

### Error 403 Forbidden
- ✅ Algunos endpoints requieren ROLE_ADMIN
- ✅ Verifica roles en Keycloak para tu usuario

## 📁 Variables de Entorno Dinámicas

Las colecciones guardan automáticamente:
- `last_user_id`: ID del último usuario creado
- `last_entrepreneurship_id`: ID del último emprendimiento creado
- `last_event_id`: ID del último evento creado
- `last_image_id`: ID de la última imagen subida
- `user_profile_image_id`: ID de la imagen de perfil del usuario
- `entrepreneurship_logo_id`: ID del logo del emprendimiento
- `event_cover_id`: ID de la portada del evento

Puedes usarlas en otras requests como `{{last_user_id}}`.

## 🔗 Comunicación entre Microservicios

### Feign Client + Circuit Breaker
Los microservicios ahora se comunican entre sí usando:
- **OpenFeign**: Cliente HTTP declarativo
- **Resilience4j**: Circuit Breaker para tolerancia a fallos
- **Fallback methods**: Respuestas alternativas cuando un servicio falla

### Ejemplos de integración:
- **user-service** → **shared-service**: Obtiene catálogos (países, tipos de contacto)
- **entrepreneurship-service** → **user-service**: Obtiene información del propietario
- **entrepreneurship-service** → **shared-service**: Obtiene categorías y ubicaciones
- **event-service** → **user-service**: Obtiene información del organizador
- **event-service** → **entrepreneurship-service**: Obtiene emprendimientos participantes
- **event-service** → **shared-service**: Obtiene tipos de evento y ubicaciones

### Documentación completa:
Ver `FEIGN_INTEGRATION_GUIDE.md` en la raíz del proyecto para detalles técnicos.

## 🔄 Storage Configuration

### Local (Development)
```yaml
storage:
  type: local
  path: ./uploads
```
Archivos servidos por: `GET /api/files/**`

### Production (Digital Ocean Spaces)
```yaml
storage:
  type: cloud
  do-spaces:
    endpoint: https://nyc3.digitaloceanspaces.com
    bucket-name: emprendia-storage
```
URLs públicas directas (NO pasan por la API).

---

**Versión**: 4.0  
**Última actualización**: Agosto 3, 2026  
**Contacto**: Kevin Guachagmira

## 🆕 Changelog

### v4.0 (Agosto 3, 2026)
- ✅ **Todo el tráfico pasa por el gateway**: las colecciones ahora apuntan a `:8090` (local) con el prefijo `/api/<servicio>` (mismo esquema que producción)
- ✅ Nuevo environment **`Emprendia-Prod`** apuntando a `https://emprendia.duckdns.org`
- ✅ Nueva variable `{{gateway_url}}` para las rutas de archivos (`/api/files/**`), que no llevan prefijo de servicio
- ✅ El CORS ahora lo maneja exclusivamente el gateway (los microservicios ya no emiten headers CORS)

### v3.0 (Mayo 8, 2026)
- ✅ Nueva colección: **05-Image Management**
- ✅ Implementación de **Feign Client** entre servicios
- ✅ Implementación de **Circuit Breaker** con Resilience4j
- ✅ DTOs compartidos para comunicación entre microservicios
- ✅ Documentación completa en `FEIGN_INTEGRATION_GUIDE.md`

### v2.0 (Mayo 5, 2026)
- ✅ Sistema de gestión de imágenes en shared-service
- ✅ Storage local y cloud (Digital Ocean Spaces)
- ✅ Límites por tipo de entidad (USER=5, ENTREPRENEURSHIP=10, EVENT=20)

### v1.0 (Abril 2026)
- ✅ Colecciones iniciales de todos los microservicios
- ✅ Integración con Keycloak OAuth2
- ✅ Tests automatizados en cada request


