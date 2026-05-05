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

## 🚀 Configuración Inicial

### 1. Importar Environment

Importa el archivo `Emprendia.postman_environment.json`:

```
keycloak_url: http://localhost:8080
keycloak_realm: emprendia
keycloak_client_id: emprendia-app
keycloak_username: admin
keycloak_password: admin

user_url: http://localhost:8081
entrepreneurship_url: http://localhost:8082
event_url: http://localhost:8083
shared_url: http://localhost:8084
```

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
2. `01-Shared > Image Gallery > Upload Image` (entityType=ENTREPRENEURSHIP, entityId={id})
3. Repetir upload hasta 10 imágenes de galería

### Crear Evento
1. `04-Event > Events > POST Crear evento`
2. `04-Event > Event Spaces > POST Crear espacio`
3. `04-Event > Event Invitations > POST Crear invitación`
4. `01-Shared > Image Gallery > Upload Image` (entityType=EVENT, entityId={id})

## 🐛 Troubleshooting

### Error 401 Unauthorized
- ✅ Verifica que `{{access_token}}` tenga valor
- ✅ Ejecuta `00-Auth > Login` si expiró (tokens duran ~5 minutos)
- ✅ Revisa que Keycloak esté corriendo en puerto 8080

### Error 404 Not Found
- ✅ Verifica que el microservicio esté ejecutándose
- ✅ Revisa el puerto en el environment (8081-8084)
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

Puedes usarlas en otras requests como `{{last_user_id}}`.

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

**Versión**: 2.0  
**Última actualización**: Mayo 2026  
**Contacto**: Kevin Guachagmira
