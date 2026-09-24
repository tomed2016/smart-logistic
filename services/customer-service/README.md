# Customer Service — Desarrollo local

Instrucciones breves para arrancar este servicio en entorno de desarrollo (Eclipse / terminal) sin depender de Postgres ni RabbitMQ.

Perfil recomendado: `local`

Qué hace el perfil `local`:
- Usa H2 en memoria (jdbc:h2:mem:...) en lugar de Postgres.
- Desactiva Flyway (no intenta migrar una BD externa).
- Desactiva el dispatcher de Outbox (`smart-logistic.outbox.enabled=false` en `application-local.yml`).
- Desactiva el health check de Rabbit (`management.health.rabbit.enabled=false`).

Construir:

PowerShell:
```
cd C:\git\smart-logistic\services\customer-service
mvn -DskipTests package
```

Ejecutar desde terminal (puede elegirse otro puerto para evitar conflictos):
```
Start-Process -NoNewWindow -FilePath java -ArgumentList '-jar','target\customer-service.jar','--spring.profiles.active=local','--server.port=18081' -PassThru
```

Ejecutar desde Eclipse:
- Crear una Run Configuration apuntando a `cl.smartlogistic.customer.CustomerServiceApplication`.
- Program arguments: `--spring.profiles.active=local`
- (Opcional) si el puerto 8081 está ocupado: `--server.port=18081`

Comprobar health:
```
Invoke-WebRequest -UseBasicParsing http://localhost:18081/actuator/health
```

Usar la pila completa (Postgres + RabbitMQ):
- Levanta la infraestructura con docker-compose (archivo raíz `infra/docker-compose.yml`).
- Quita `--spring.profiles.active=local` para que la app use `application.yml` por defecto y se conecte a Postgres/RabbitMQ.

Notas:
- Existe una clase `LocalRabbitConfig` que provee un `RabbitTemplate` no-op para desarrollos. No es necesaria si `smart-logistic.outbox.enabled=false`.
- Si prefieres que el outbox se ejecute en local, quita la propiedad `smart-logistic.outbox.enabled: false` y levanta RabbitMQ localmente.
