# User Service - Dominio de Usuarios

## Descripción

Microservicio encargado de la gestión de usuarios del sistema. Los usuarios se autentican vía Keycloak y son referenciados por su `keycloak_id` en todos los demás dominios.

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

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/v1/users` | Listar todos los usuarios |
| GET | `/api/v1/users/{id}` | Obtener usuario por ID interno |
| GET | `/api/v1/users/keycloak/{keycloakId}` | Obtener usuario por Keycloak ID |
| POST | `/api/v1/users` | Crear usuario |
| PUT | `/api/v1/users/{id}` | Actualizar usuario |
| DELETE | `/api/v1/users/{id}` | Eliminar usuario |

---

## Estructura de Carpetas

```
user-service/src/main/java/com/project/emprendia/user/
├── configuration/
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
│   ├── UserResponse.java
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
│       └── UserServiceImpl.java
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
