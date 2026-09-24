# Geo Catalog Service — Desarrollo local

Instrucciones rápidas para ejecutar este servicio en desarrollo (sin Postgres ni RabbitMQ).

Perfil recomendado: `local`

Comportamiento del perfil `local`:
- Usa H2 en memoria (jdbc:h2:mem:...) en lugar de Postgres.
- Desactiva Flyway.
- Desactiva outbox (`smart-logistic.outbox.enabled=false`) por consistencia con `customer-service`.
- Desactiva el health check de Rabbit (`management.health.rabbit.enabled=false`).

Construir:
```
cd C:\git\smart-logistic\services\geo-catalog-service
mvn -DskipTests package
```

Ejecutar en terminal (puerto alternativo para evitar conflictos):
```
Start-Process -NoNewWindow -FilePath java -ArgumentList '-jar','target\geo-catalog-service.jar','--spring.profiles.active=local','--server.port=18082' -PassThru
```

Ejecutar desde Eclipse:
- Run Configuration apuntando a `cl.smartlogistic.geo.GeoCatalogServiceApplication`.
- Program arguments: `--spring.profiles.active=local` (añadir `--server.port=18082` si 8082 está ocupado).

Levantar la pila completa para E2E:
- Usa `infra/docker-compose.yml` en la raíz del repositorio para levantar Postgres y RabbitMQ. Quita `--spring.profiles.active=local` para que la app use la configuración por defecto.

Notas:
- `application-local.yml` está preparado para desarrollo local. Si necesitas el comportamiento de producción, usa `application.yml` y la infraestructura adecuada.
