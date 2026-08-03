# Emprendia – Docker Local

## Requisitos

- Docker Engine 24+
- Docker Compose v2

## Puerto Mappings

| Servicio | Puerto Host | Puerto Container |
|---|---|---|
| PostgreSQL | 5433 | 5432 |
| Keycloak | 8080 | 8080 |
| user-service | 8081 | 8081 |
| entrepreneurship-service | 8082 | 8082 |
| event-service | 8083 | 8083 |
| shared-service | 8084 | 8084 |

## Uso

```bash
# 1. Ir a la carpeta docker
cd docker

# 2. Construir y levantar todo
docker compose up -d

# 3. Ver logs
docker compose logs -f

# 4. Verificar health
docker compose ps

# 5. Detener
docker compose down

# 6. Detener y borrar volúmenes (BD desde cero)
docker compose down -v
```

## Orden de Inicio

1. `postgres` → base de datos
2. `keycloak` → identity provider (usa postgres)
3. `shared-service` → catálogos + imágenes
4. `user-service` → usuarios (depende de shared-service + keycloak)
5. `entrepreneurship-service` (depende de shared-service + user-service)
6. `event-service` (depende de todos)

Las dependencias están declaradas en docker-compose.yml con `depends_on` y healthchecks.

## Notas

- **Keycloak**: Después del primer `up`, configurar realm `emprendia` en `http://localhost:8080`.
  Admin: `admin` / `admin`
- **Base de datos**: Se crean 2 bases: `emprendia_db` y `keycloak_db`
- **Storage**: shared-service usa almacenamiento local montado en el volumen `shared_uploads`
- **Mail**: configurado via Gmail SMTP (las credenciales están en `.env`)