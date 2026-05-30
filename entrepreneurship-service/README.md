# Entrepreneurship Service - Dominio de Emprendimientos

## Descripción

Microservicio que gestiona los emprendimientos de la plataforma: negocios, categorías, ubicaciones físicas, redes sociales, galería de imágenes y portales personalizados.

**Integración con Shared Service**: Obtiene automáticamente el logo del emprendimiento desde el servicio de imágenes.

## Puerto: `8082`
## Package Base: `com.project.emprendia.entrepreneurship`

---

## Dominio

### Entidades

| Entidad | Tabla | Descripción |
|---|---|---|
| `Category` | `category` | Categorías de emprendimientos |
| `Entrepreneurship` | `entrepreneurship` | Negocio principal |
| `EntrepreneurshipLocation` | `entrepreneurship_location` | Ubicación física con GPS |
| `EntrepreneurshipSocialLink` | `entrepreneurship_social_link` | Redes sociales |
| `EntrepreneurshipGallery` | `entrepreneurship_gallery` | Galería de imágenes |
| `EntrepreneurshipPortal` | `entrepreneurship_portal` | Portal/sitio web personalizado |

### Enumeraciones

| Enum | Valores |
|---|---|
| `SocialPlatform` | `FACEBOOK`, `INSTAGRAM`, `WHATSAPP`, `TIKTOK`, `TWITTER` |
| `PortalTheme` | `LIGHT`, `DARK` |

---

## Endpoints REST

### Entrepreneurships

| Método | URL | Descripción | Incluye Imagen | Paginado |
|---|---|---|---|---|
| GET | `/api/v1/entrepreneurships` | Listar todos | ❌ | ❌ |
| GET | `/api/v1/entrepreneurships/{id}` | Obtener por ID | ❌ | ❌ |
| GET | `/api/v1/entrepreneurships/user/{userId}` | Listar por usuario | ✅ | ❌ |
| GET | `/api/v1/entrepreneurships/search` | Búsqueda con filtros | ✅ | ✅ Opcional |
| POST | `/api/v1/entrepreneurships` | Crear emprendimiento | ❌ | ❌ |
| PUT | `/api/v1/entrepreneurships/{id}` | Actualizar | ❌ | ❌ |
| DELETE | `/api/v1/entrepreneurships/{id}` | Eliminar (cascade) | ❌ | ❌ |

### Entrepreneurship Locations

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/entrepreneurship-locations/entrepreneurship/{entrepreneurshipId}` | Listar ubicaciones de un emprendimiento |
| GET | `/api/v1/entrepreneurship-locations/{id}` | Obtener ubicación por ID |
| POST | `/api/v1/entrepreneurship-locations` | Crear nueva ubicación física |
| PUT | `/api/v1/entrepreneurship-locations/{id}` | Actualizar ubicación |
| DELETE | `/api/v1/entrepreneurship-locations/{id}` | Eliminar ubicación |

### Entrepreneurship Social Links

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/entrepreneurship-social-links/entrepreneurship/{entrepreneurshipId}` | Listar redes sociales |
| GET | `/api/v1/entrepreneurship-social-links/{id}` | Obtener red social por ID |
| POST | `/api/v1/entrepreneurship-social-links` | Agregar red social |
| PUT | `/api/v1/entrepreneurship-social-links/{id}` | Actualizar red social |
| DELETE | `/api/v1/entrepreneurship-social-links/{id}` | Eliminar red social |

### Entrepreneurship Portals

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/entrepreneurship-portals/entrepreneurship/{entrepreneurshipId}` | Obtener portal de un emprendimiento |
| GET | `/api/v1/entrepreneurship-portals/{id}` | Obtener portal por ID |
| POST | `/api/v1/entrepreneurship-portals` | Crear portal web personalizado |
| PUT | `/api/v1/entrepreneurship-portals/{id}` | Actualizar portal |
| DELETE | `/api/v1/entrepreneurship-portals/{id}` | Eliminar portal |

### Categories

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/categories` | Listar |
| GET | `/api/v1/categories/{id}` | Por ID |
| GET | `/api/v1/categories/name/{name}` | Por nombre |
| POST | `/api/v1/categories` | Crear |
| PUT | `/api/v1/categories/{id}` | Actualizar |
| DELETE | `/api/v1/categories/{id}` | Eliminar |

### Parámetros de Búsqueda

| Parámetro | Tipo | Descripción |
|---|---|---|
| `name` | String | Búsqueda parcial por nombre |
| `categoryId` | Long | Filtro por categoría |
| `isPhysical` | Boolean | Filtro por presencia física |
| `isDigital` | Boolean | Filtro por presencia digital |
| `page` | Integer | Número de página (opcional, para paginación) |
| `size` | Integer | Tamaño de página (opcional, para paginación) |

### Ejemplos de Búsqueda

**Sin paginación** (retorna `List<EntrepreneurshipResponse>`):
```
GET /api/v1/entrepreneurships/search?name=panaderia&categoryId=1
```

**Con paginación** (retorna `Page<EntrepreneurshipResponse>`):
```
GET /api/v1/entrepreneurships/search?name=panaderia&page=0&size=10
```

### Campos de EntrepreneurshipResponse

```json
{
  "entrepreneurshipId": 1,
  "userId": 1,
  "categoryId": 2,
  "categoryName": "Alimentos",
  "name": "Panadería Artesanal",
  "description": "Pan hecho a mano",
  "logoUrl": "legacy-field",
  "isPhysical": true,
  "isDigital": false,
  "imageUrl": "http://localhost:8084/api/files/entrepreneurships/1/logo.jpg",
  "imageId": 5,
  "createdAt": "2026-05-29T10:00:00",
  "updatedAt": "2026-05-29T10:00:00"
}
```

**Nota**: 
- `imageUrl` y `imageId` se obtienen del **shared-service** (displayOrder=0)
- Si el emprendimiento no tiene logo, ambos campos son `null`
- `logoUrl` es un campo legacy (no se usa actualmente)

---

## Integración con Shared Service

### Propagación de Token JWT

El servicio utiliza `FeignClientConfiguration` para propagar automáticamente el token JWT a todas las llamadas al shared-service.

### Obtención de Logo

En endpoints `/user/{userId}` y `/search`, el servicio:
1. Obtiene los emprendimientos de la BD
2. Llama a `shared-service` para obtener imágenes del emprendimiento
3. Busca la imagen con `displayOrder=0` (logo principal)
4. Enriquece el response con `imageUrl` e `imageId`

Si shared-service no responde, se usa Circuit Breaker y no se rompe el servicio.

---

## Estructura de Carpetas

```
entrepreneurship-service/src/main/java/com/project/emprendia/entrepreneurship/
├── client/
│   ├── SharedServiceClient.java           ← Comunicación con shared-service
│   └── UserServiceClient.java
├── configuration/
│   ├── FeignClientConfiguration.java      ← Propagación de JWT ⭐
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   └── KeycloakJwtAuthenticationConverter.java
├── controller/
│   └── EntrepreneurshipController.java    ← Soporte de paginación
├── domain/
│   ├── Category.java
│   ├── Entrepreneurship.java
│   ├── EntrepreneurshipLocation.java
│   ├── EntrepreneurshipSocialLink.java
│   ├── EntrepreneurshipGallery.java
│   └── EntrepreneurshipPortal.java
├── dto/
│   ├── CategoryRequest.java
│   ├── CategoryResponse.java
│   ├── EntrepreneurshipRequest.java
│   ├── EntrepreneurshipResponse.java      ← Incluye imageUrl e imageId
│   ├── ImageInfo.java
│   └── ImageGalleryResponse.java
├── enums/
│   ├── SocialPlatform.java
│   └── PortalTheme.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── DuplicateResourceException.java
├── mapping/mapper/
│   └── EntrepreneurshipMapper.java
├── repository/
│   ├── CategoryRepository.java
│   ├── EntrepreneurshipRepository.java
│   └── EntrepreneurshipQueryRepository.java  ← Soporte de paginación
├── service/
│   ├── EntrepreneurshipService.java          ← searchPaginated()
│   └── impl/
│       └── EntrepreneurshipServiceImpl.java  ← Enriquecimiento con imágenes
└── util/
    └── DateUtil.java
```

---

## Changelog

### v1.2.0 - 29 Mayo 2026
- ✅ Implementado CRUD completo para EntrepreneurshipLocation
- ✅ Implementado CRUD completo para EntrepreneurshipSocialLink
- ✅ Implementado CRUD completo para EntrepreneurshipPortal
- ✅ Documentado Swagger/OpenAPI para todos los nuevos endpoints
- ✅ Agregada eliminación en cascada de entidades relacionadas
- ✅ Validación de existencia de emprendimientos en todas las operaciones
- ✅ Validación de subdominio único en portales
---

## Eliminación en Cascada

Al eliminar un emprendimiento (`DELETE /api/v1/entrepreneurships/{id}`), se eliminan automáticamente:
- ✅ Todas las ubicaciones físicas (`EntrepreneurshipLocation`)
- ✅ Todas las redes sociales (`EntrepreneurshipSocialLink`)
- ✅ El portal web si existe (`EntrepreneurshipPortal`)

Esto se logra mediante `CascadeType.ALL` en las relaciones `@OneToMany` y `@OneToOne`.

---

## Ejemplos de Uso Completo

### Crear Emprendimiento con Información Completa

```bash
# 1. Crear emprendimiento base
POST /api/v1/entrepreneurships
{
  "userId": 1,
  "categoryId": 2,
  "name": "Panadería Artesanal",
  "description": "Pan casero hecho con ingredientes orgánicos",
  "isPhysical": true,
  "isDigital": false
}
→ Respuesta: { entrepreneurshipId: 5 }

# 2. Subir logo (shared-service)
POST /api/images/upload
Form-data:
  - file: logo.jpg
  - entityType: ENTREPRENEURSHIP
  - entityId: 5
  - displayOrder: 0

# 3. Agregar ubicación física
POST /api/v1/entrepreneurship-locations
{
  "entrepreneurshipId": 5,
  "countryId": 1,
  "provinceId": 2,
  "cityId": 15,
  "parishId": 100,
  "addressLine": "Av. Principal 123 y Secundaria",
  "latitude": -0.123456,
  "longitude": -78.654321
}

# 4. Agregar redes sociales
POST /api/v1/entrepreneurship-social-links
{
  "entrepreneurshipId": 5,
  "socialPlatformId": 1,  // Facebook
  "url": "https://facebook.com/panaderia.artesanal"
}

POST /api/v1/entrepreneurship-social-links
{
  "entrepreneurshipId": 5,
  "socialPlatformId": 2,  // Instagram
  "url": "https://instagram.com/panaderia_artesanal"
}

# 5. Crear portal web personalizado
POST /api/v1/entrepreneurship-portals
{
  "entrepreneurshipId": 5,
  "subdomain": "panaderia-artesanal",
  "themeId": 1,
  "isActive": true
}
→ Portal accesible en: https://panaderia-artesanal.emprendia.com
```

### Consultar Emprendimiento con Toda su Información

```bash
# 1. Obtener datos básicos
GET /api/v1/entrepreneurships/5

# 2. Obtener ubicaciones
GET /api/v1/entrepreneurship-locations/entrepreneurship/5

# 3. Obtener redes sociales
GET /api/v1/entrepreneurship-social-links/entrepreneurship/5

# 4. Obtener portal
GET /api/v1/entrepreneurship-portals/entrepreneurship/5

# 5. Obtener galería de imágenes
GET /api/images?entityType=ENTREPRENEURSHIP&entityId=5
```


### v1.1.0 - 29 Mayo 2026
- ✅ Agregada integración con shared-service para logos de emprendimientos
- ✅ Creado `FeignClientConfiguration` para propagación automática de JWT
- ✅ Agregados campos `imageUrl` e `imageId` en `EntrepreneurshipResponse`
- ✅ Implementado enriquecimiento automático en `/user/{userId}` y `/search`
- ✅ Agregada paginación opcional en `/search` (parámetros `page` y `size`)
- ✅ Refactorizado `EntrepreneurshipQueryRepository` con soporte de paginación

