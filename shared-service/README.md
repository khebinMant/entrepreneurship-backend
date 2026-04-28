# Shared Service - Dominio Compartido

## Descripción

Microservicio que gestiona los catálogos y valores de referencia compartidos entre todos los otros microservicios. Actúa como la fuente de verdad para datos configurables como tipos de documentos, países, ciudades, plataformas sociales, etc.

## Puerto: `8084`
## Package Base: `com.project.emprendia.shared`

---

## Dominio

### Entidades

| Entidad | Tabla | Descripción |
|---|---|---|
| `CatalogueType` | `catalogue_type` | Tipos de catálogo (COUNTRY, CITY, etc.) |
| `CatalogueValue` | `catalogue_value` | Valores de cada catálogo con soporte jerárquico |

### Tipos de Catálogo Disponibles

| Código | Descripción |
|---|---|
| `COUNTRY` | Países |
| `PROVINCE` | Provincias |
| `CITY` | Ciudades |
| `PARISH` | Parroquias |
| `CONTACT_TYPE` | Tipos de contacto (Phone, Email) |
| `IDENTIFICATION_TYPE` | Tipos de identificación (Cédula, RUC, Passport) |
| `EVENT_TYPE` | Tipos de evento (Physical, Virtual) |
| `EVENT_VISIBILITY` | Visibilidad de evento (Public, Private) |
| `INVITATION_STATUS` | Estados de invitación (Pending, Accepted, Rejected) |
| `SOCIAL_PLATFORM` | Redes sociales (Facebook, Instagram, etc.) |
| `THEME_TYPE` | Temas de portal (Light, Dark) |
| `EVENT_PARTICIPATION_STATUS` | Estados de participación |

---

## Endpoints REST

### Catalogue Types

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/catalogue-types` | Listar todos los tipos |
| GET | `/api/v1/catalogue-types/{id}` | Obtener por ID |
| POST | `/api/v1/catalogue-types` | Crear tipo |
| PUT | `/api/v1/catalogue-types/{id}` | Actualizar tipo |
| DELETE | `/api/v1/catalogue-types/{id}` | Eliminar tipo |

### Catalogue Values

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/catalogue-values/by-type/{typeCode}` | Listar valores por tipo |
| GET | `/api/v1/catalogue-values/{id}` | Obtener por ID |
| GET | `/api/v1/catalogue-values/{id}/children` | Obtener hijos (jerarquía) |
| POST | `/api/v1/catalogue-values` | Crear valor |
| PUT | `/api/v1/catalogue-values/{id}` | Actualizar valor |
| DELETE | `/api/v1/catalogue-values/{id}` | Eliminar valor |

---

## Ejemplo de Uso

```bash
# Obtener todas las provincias del Ecuador
GET /api/v1/catalogue-values/by-type/PROVINCE

# Obtener ciudades hijas de la provincia Pichincha (id: 5)
GET /api/v1/catalogue-values/5/children
```

---

## Estructura de Carpetas

```
shared-service/src/main/java/com/project/emprendia/shared/
├── configuration/
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   └── KeycloakJwtAuthenticationConverter.java
├── controller/
│   ├── CatalogueTypeController.java
│   └── CatalogueValueController.java
├── domain/
│   ├── CatalogueType.java
│   └── CatalogueValue.java
├── dto/
│   ├── CatalogueTypeRequest.java
│   ├── CatalogueTypeResponse.java
│   ├── CatalogueValueRequest.java
│   └── CatalogueValueResponse.java
├── enums/
│   └── CatalogueTypeCode.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── DuplicateResourceException.java
├── mapping/mapper/
│   ├── CatalogueTypeMapper.java
│   └── CatalogueValueMapper.java
├── repository/
│   ├── CatalogueTypeRepository.java
│   ├── CatalogueValueRepository.java
│   └── CatalogueValueQueryRepository.java
├── service/
│   ├── CatalogueTypeService.java
│   ├── CatalogueValueService.java
│   └── impl/
│       ├── CatalogueTypeServiceImpl.java
│       └── CatalogueValueServiceImpl.java
└── util/
    └── DateUtil.java
```
