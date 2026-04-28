# Entrepreneurship Service - Dominio de Emprendimientos

## Descripción

Microservicio que gestiona los emprendimientos de la plataforma: negocios, categorías, ubicaciones físicas, redes sociales, galería de imágenes y portales personalizados.

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

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/entrepreneurships` | Listar todos |
| GET | `/api/v1/entrepreneurships/{id}` | Obtener por ID |
| GET | `/api/v1/entrepreneurships/user/{userId}` | Listar por usuario |
| GET | `/api/v1/entrepreneurships/search` | Búsqueda con filtros |
| POST | `/api/v1/entrepreneurships` | Crear emprendimiento |
| PUT | `/api/v1/entrepreneurships/{id}` | Actualizar |
| DELETE | `/api/v1/entrepreneurships/{id}` | Eliminar |

### Parámetros de Búsqueda

| Parámetro | Tipo | Descripción |
|---|---|---|
| `name` | String | Búsqueda parcial por nombre |
| `categoryId` | Long | Filtro por categoría |
| `isPhysical` | Boolean | Filtro por presencia física |
| `isDigital` | Boolean | Filtro por presencia digital |

---

## Estructura de Carpetas

```
entrepreneurship-service/src/main/java/com/project/emprendia/entrepreneurship/
├── configuration/
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   └── KeycloakJwtAuthenticationConverter.java
├── controller/
│   └── EntrepreneurshipController.java
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
│   └── EntrepreneurshipResponse.java
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
│   └── EntrepreneurshipQueryRepository.java
├── service/
│   ├── EntrepreneurshipService.java
│   └── impl/
│       └── EntrepreneurshipServiceImpl.java
└── util/
    └── DateUtil.java
```
