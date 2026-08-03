# Emprendia – Docker Local (solo microservicios)

Para cuando ya tenés PostgreSQL y Keycloak corriendo localmente (no Docker o en sus propios containers).

## Requisitos

- PostgreSQL en `localhost:5433`
- Keycloak en `localhost:8080` con realm `emprendia`
- Docker Engine 24+

## Uso

```bash
cd docker-local
docker compose up -d --build
```

## Diferencia con `docker/`

| Carpeta | Incluye | Para qué |
|---|---|---|
| `docker/` | PG + KC + 4 micros | VPS, despliegue completo |
| `docker-local/` | Solo los 4 micros | Pruebas locales con tu PG y KC existentes |

Los microservicios se conectan a tu PG y KC locales via `host.docker.internal`.