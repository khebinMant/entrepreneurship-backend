# User Service - Dominio de Usuarios

## Descripción

Microservicio encargado de la gestión de usuarios del sistema. Los usuarios se autentican vía Keycloak y son referenciados por su `keycloak_id` en todos los demás dominios.

**Integración con Shared Service**: Obtiene automáticamente la foto de perfil del usuario desde el servicio de imágenes.

## Puerto: `8081`
## Package Base: `com.project.emprendia.user`

---

## Dominio

### Entidades

| Entidad | Tabla | Descripción |
|---|---|---|
| `AppUser` | `app_user` | Usuario principal con referencia a Keycloak |
| `UserContact` | `user_contact` | Teléfonos y emails del usuario |
| `UserAddress` | `user_address` | Direcciones físicas del usuario |
| `UserIdentification` | `user_identification` | Documentos de identidad |

### Enumeraciones

| Enum | Valores |
|---|---|
| `ContactType` | `PHONE`, `EMAIL` |
| `IdentificationType` | `CEDULA`, `RUC`, `PASSPORT` |

---

## Endpoints REST

### Users

| Método | URL | Descripción | Incluye Imagen |
|---|---|---|---|
| GET | `/api/v1/users` | Listar todos los usuarios | ✅ |
| GET | `/api/v1/users/{id}` | Obtener usuario por ID interno | ✅ |
| GET | `/api/v1/users/keycloak/{keycloakId}` | Obtener usuario por Keycloak ID | ✅ |
| POST | `/api/v1/users` | Crear usuario | ❌ |
| PUT | `/api/v1/users/{id}` | Actualizar usuario | ❌ |
| DELETE | `/api/v1/users/{id}` | Eliminar usuario | ❌ |

### Campos de UserResponse

```json
{
  "userId": 1,
  "keycloakId": "uuid-123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "profilePictureUrl": "legacy-field",
  "imageUrl": "http://localhost:8084/api/files/users/1/profile.jpg",
  "imageId": 3,
  "createdAt": "2026-05-29T10:00:00",
  "updatedAt": "2026-05-29T10:00:00"
}
```

**Nota**: 
- `imageUrl` y `imageId` se obtienen del **shared-service** (displayOrder=0)
- Si el usuario no tiene foto, ambos campos son `null`
- `profilePictureUrl` es un campo legacy (no se usa actualmente)

---

## Integración con Shared Service

### Propagación de Token JWT

El servicio utiliza `FeignClientConfiguration` para propagar automáticamente el token JWT a todas las llamadas al shared-service.

### Obtención de Imagen de Perfil

En cada consulta GET de usuario, el servicio:
1. Obtiene los datos del usuario de la BD
2. Llama a `shared-service` para obtener imágenes del usuario
3. Busca la imagen con `displayOrder=0` (foto de perfil principal)
4. Enriquece el response con `imageUrl` e `imageId`

Si shared-service no responde, se usa Circuit Breaker y no se rompe el servicio.

---

## Estructura de Carpetas

```
user-service/src/main/java/com/project/emprendia/user/
├── client/
│   └── SharedServiceClient.java          ← Comunicación con shared-service
├── configuration/
│   ├── FeignClientConfiguration.java      ← Propagación de JWT ⭐
│   ├── QueryDslConfig.java
│   ├── SecurityConfig.java
│   └── KeycloakJwtAuthenticationConverter.java
├── controller/
│   └── UserController.java
├── domain/
│   ├── AppUser.java
│   ├── UserContact.java
│   ├── UserAddress.java
│   └── UserIdentification.java
├── dto/
│   ├── UserRequest.java
│   ├── UserResponse.java                  ← Incluye imageUrl e imageId
│   ├── UserContactRequest.java
│   ├── UserContactResponse.java
│   ├── UserAddressResponse.java
│   ├── UserIdentificationRequest.java
│   └── UserIdentificationResponse.java
├── enums/
│   ├── ContactType.java
│   └── IdentificationType.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── DuplicateResourceException.java
├── mapping/mapper/
│   └── UserMapper.java
├── repository/
│   ├── UserRepository.java
│   ├── UserQueryRepository.java
│   ├── UserContactRepository.java
│   ├── UserAddressRepository.java
│   └── UserIdentificationRepository.java
├── service/
│   ├── UserService.java
│   └── impl/
│       └── UserServiceImpl.java           ← Enriquecimiento con imágenes
└── util/
    └── DateUtil.java
```

---

## Flujo de Registro de Usuario

1. El cliente obtiene un token JWT de Keycloak
2. Llama `POST /api/v1/users` con el `keycloak_id` del token
3. El servicio verifica que no exista un usuario con ese `keycloak_id`
4. Crea el registro en `app_user`
5. Opcionalmente se agregan contactos, direcciones e identificaciones
6. **Para agregar foto de perfil**: Usar `shared-service` → `POST /api/images/upload` con `entityType=USER`, `entityId={userId}`, `displayOrder=0`

---

## Changelog

### v1.1.0 - 29 Mayo 2026
- ✅ Agregada integración con shared-service para imágenes de perfil
- ✅ Creado `FeignClientConfiguration` para propagación automática de JWT
- ✅ Agregados campos `imageUrl` e `imageId` en `UserResponse`
- ✅ Implementado enriquecimiento automático en endpoints GET
