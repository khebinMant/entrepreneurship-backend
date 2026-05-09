# Shared Service - Catálogos e Imágenes
**Puerto**: 8084 | **Package**: `com.project.emprendia.shared`

---

## 📋 Contenido

1. [Descripción](#descripción)
2. [Endpoints REST](#endpoints-rest)
3. [Sistema de Imágenes](#sistema-de-imágenes)
4. [Perfiles de Configuración](#perfiles-de-configuración)
5. [Estructura del Proyecto](#estructura-del-proyecto)

---

## 📝 Descripción

Microservicio que gestiona:
- **Catálogos compartidos**: Datos de referencia para todos los servicios
- **Sistema de imágenes**: Almacenamiento y gestión de imágenes con soporte local y cloud

### Tipos de Catálogo Disponibles

| Código | Descripción |
|--------|-------------|
| `COUNTRY` | Países |
| `PROVINCE` | Provincias |
| `CITY` | Ciudades |
| `PARISH` | Parroquias |
| `CONTACT_TYPE` | Tipos de contacto (Phone, Email) |
| `IDENTIFICATION_TYPE` | Tipos de identificación (Cédula, RUC, Passport) |
| `EVENT_TYPE` | Tipos de evento (Physical, Virtual) |
| `EVENT_VISIBILITY` | Visibilidad de evento (Public, Private) |
| `INVITATION_STATUS` | Estados de invitación |
| `SOCIAL_PLATFORM` | Redes sociales (Facebook, Instagram, etc.) |
| `THEME_TYPE` | Temas de portal (Light, Dark) |

---

## 📡 Endpoints REST

### Catalogue Types

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/catalogue-types` | Listar todos | Público |
| GET | `/api/v1/catalogue-types/{id}` | Por ID | Público |
| POST | `/api/v1/catalogue-types` | Crear | ADMIN |
| PUT | `/api/v1/catalogue-types/{id}` | Actualizar | ADMIN |
| DELETE | `/api/v1/catalogue-types/{id}` | Eliminar | ADMIN |

### Catalogue Values

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/catalogue-values/by-type/{typeCode}` | Por tipo | Público |
| GET | `/api/v1/catalogue-values/{id}` | Por ID | Público |
| GET | `/api/v1/catalogue-values/{id}/children` | Hijos (jerarquía) | Público |
| POST | `/api/v1/catalogue-values` | Crear | ADMIN |
| PUT | `/api/v1/catalogue-values/{id}` | Actualizar | ADMIN |
| DELETE | `/api/v1/catalogue-values/{id}` | Eliminar | ADMIN |

### Image Gallery

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

**Ejemplo de uso:**
```bash
# Obtener provincias de Ecuador
GET /api/v1/catalogue-values/by-type/PROVINCE

# Obtener ciudades de Pichincha (id: 5)
GET /api/v1/catalogue-values/5/children

# Subir imagen
POST /api/images/upload
Form-data: file, entityType=USER, entityId=1
```

---

## 📸 Sistema de Imágenes

### Arquitectura

```
Strategy Pattern
     │
     ▼
StorageService (interface)
     │
     ├──► LocalStorageService      (storage.type=local)
     │    └── ./uploads/
     │
     └──► CloudStorageService      (storage.type=cloud)
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
- **Nombre**: `{timestamp}_{uuid}.{extension}`

### Estructura de Storage

```
uploads/ (o bucket en cloud)
├── users/{userId}/
│   ├── profile.jpg              ← displayOrder=0
│   └── gallery/*.jpg            ← displayOrder>0
├── entrepreneurships/{id}/
│   ├── logo.jpg
│   └── gallery/*.jpg
└── events/{id}/
    ├── cover.jpg
    └── gallery/*.jpg
```

### Tabla: image_gallery

```sql
CREATE TABLE image_gallery (
  image_id SERIAL PRIMARY KEY,
  entity_type VARCHAR(50) NOT NULL,  -- USER | ENTREPRENEURSHIP | EVENT
  entity_id BIGINT NOT NULL,
  image_url TEXT NOT NULL,
  file_name VARCHAR(255),
  display_order INT,
  alt_text VARCHAR(255),
  description TEXT,
  width_px INT,
  height_px INT,
  file_size_kb INT,
  mime_type VARCHAR(50),
  uploaded_by_user_id BIGINT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

---

## ⚙️ Perfiles de Configuración

### 1. Local (Desarrollo) - Por defecto

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

**Activar:**
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

**Características:**
- ✅ Almacenamiento local en `./uploads/`
- ✅ No requiere credenciales cloud
- ✅ Base de datos local
- ✅ Logs SQL visibles

### 2. Local-Cloud (Testing Cloud)

```yaml
spring:
  profiles:
    active: local-cloud
  datasource:
    url: jdbc:postgresql://localhost:5433/postgres  # Local

storage:
  type: cloud  # Cloud!
  cloud:
    endpoint: ${DO_SPACES_ENDPOINT}
    access-key: ${DO_SPACES_ACCESS_KEY}
    secret-key: ${DO_SPACES_SECRET_KEY}
    bucket-name: ${DO_SPACES_BUCKET_NAME}
```

**Activar:**
```bash
# Configurar variables
$env:DO_SPACES_ACCESS_KEY="your-key"
$env:DO_SPACES_SECRET_KEY="your-secret"
$env:DO_SPACES_BUCKET_NAME="your-bucket"

# Ejecutar
./gradlew bootRun --args='--spring.profiles.active=local-cloud'
```

**Características:**
- ✅ Storage en Digital Ocean Spaces
- ✅ Base de datos local (fácil debug)
- ✅ Prueba integración cloud sin deploy

### 3. Producción

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

**Variables de entorno requeridas:**
```bash
export DB_URL=jdbc:postgresql://prod-host:5432/emprendia_db
export DB_USERNAME=emprendia_user
export DB_PASSWORD=secure_password
export KEYCLOAK_ISSUER_URI=https://keycloak.prod.com/realms/emprendia
export DO_SPACES_ENDPOINT=https://nyc3.digitaloceanspaces.com
export DO_SPACES_ACCESS_KEY=your-key
export DO_SPACES_SECRET_KEY=your-secret
export DO_SPACES_BUCKET_NAME=emprendia-bucket
```

**Activar:**
```bash
java -jar shared-service.jar --spring.profiles.active=prod
```

### Resumen de Perfiles

| Perfil | Storage | Base de Datos | Uso |
|--------|---------|---------------|-----|
| `local` | Local (./uploads) | Local | Desarrollo diario |
| `local-cloud` | Cloud (DO Spaces) | Local | Testing cloud |
| `prod` | Cloud (DO Spaces) | Remota | Producción |

---

## 🗂️ Estructura del Proyecto

```
shared-service/src/main/java/com/project/emprendia/shared/
├── configuration/
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   └── KeycloakJwtAuthenticationConverter.java
├── controller/
│   ├── CatalogueTypeController.java
│   ├── CatalogueValueController.java
│   ├── ImageGalleryController.java
│   └── FileServeController.java
├── domain/
│   ├── CatalogueType.java
│   ├── CatalogueValue.java
│   └── ImageGallery.java
├── dto/
│   ├── CatalogueTypeRequest.java
│   ├── CatalogueTypeResponse.java
│   ├── CatalogueValueRequest.java
│   ├── CatalogueValueResponse.java
│   ├── ImageGalleryRequest.java
│   ├── ImageGalleryResponse.java
│   ├── ImageUploadRequest.java
│   └── ImageUploadResponse.java
├── enums/
│   ├── CatalogueTypeCode.java
│   └── EntityType.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── StorageException.java
├── mapping/mapper/
│   ├── CatalogueTypeMapper.java
│   ├── CatalogueValueMapper.java
│   └── ImageGalleryMapper.java
├── repository/
│   ├── CatalogueTypeRepository.java
│   ├── CatalogueValueRepository.java
│   ├── CatalogueValueQueryRepository.java
│   └── ImageGalleryRepository.java
├── service/
│   ├── CatalogueTypeService.java
│   ├── CatalogueValueService.java
│   ├── ImageGalleryService.java
│   └── impl/
│       ├── CatalogueTypeServiceImpl.java
│       ├── CatalogueValueServiceImpl.java
│       └── ImageGalleryServiceImpl.java
├── storage/
│   ├── StorageService.java              (Interface)
│   ├── StorageFile.java
│   ├── StorageResult.java
│   └── impl/
│       ├── LocalStorageService.java
│       └── CloudStorageService.java
└── util/
    └── DateUtil.java
```

---

## 🚀 Quick Start

### Desarrollo Local
```bash
# Compilar
./gradlew build

# Ejecutar
./gradlew bootRun --args='--spring.profiles.active=local'

# Verificar
curl http://localhost:8084/actuator/health
```

### Testing Cloud
```bash
# Configurar DO Spaces
$env:DO_SPACES_ACCESS_KEY="your-key"
$env:DO_SPACES_SECRET_KEY="your-secret"
$env:DO_SPACES_BUCKET_NAME="your-bucket"

# Ejecutar
./gradlew bootRun --args='--spring.profiles.active=local-cloud'
```

### Producción
```bash
# Configurar todas las variables de entorno
# (Ver sección "Perfiles de Configuración" arriba)

# Ejecutar JAR
java -jar shared-service.jar --spring.profiles.active=prod
```

---

## 🔒 Seguridad

### Endpoints Públicos
- `GET /api/v1/catalogue-types/**`
- `GET /api/v1/catalogue-values/**`
- `GET /api/images` (lectura)
- `GET /api/files/**`

### Endpoints Protegidos
- `POST/PUT/DELETE /api/v1/catalogue-*` → Requiere `ROLE_ADMIN`
- `POST/PUT/DELETE /api/images/**` → Requiere JWT válido

---

## 🐛 Troubleshooting

### Error: "Could not create directory"
**Causa**: Sin permisos para crear `./uploads/`  
**Solución**: Dar permisos de escritura o cambiar `storage.local.path`

### Error: "Invalid AWS credentials"
**Causa**: Credenciales de Digital Ocean incorrectas  
**Solución**: Verificar `DO_SPACES_ACCESS_KEY` y `DO_SPACES_SECRET_KEY`

### Error: "File size exceeds maximum"
**Causa**: Archivo mayor a 5MB  
**Solución**: Comprimir imagen antes de subir

### Error: "Image limit reached"
**Causa**: Límite alcanzado (USER=5, ENTREPRENEURSHIP=10, EVENT=20)  
**Solución**: Eliminar imágenes existentes primero

---

**Autor**: Kevin Guachagmira  
**Email**: kguachag@pichincha.com  
**Proyecto**: Plataforma Emprendia - Tesis NIBE  
**Versión**: 3.0 | Mayo 8, 2026

