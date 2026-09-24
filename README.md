# smart-logistic

## Smoke test Docker Compose

Con Docker Desktop en ejecucion, el smoke test levanta el stack completo, espera
los healthchecks, consulta la comuna INE `13119` desde `geo-catalog-service` y
crea un cliente con una direccion en `customer-service`. Al finalizar elimina
contenedores y volumenes del proyecto de prueba:

```powershell
.\infra\docker-compose-smoke-test.ps1
```

Para reutilizar imagenes ya construidas:

```powershell
.\infra\docker-compose-smoke-test.ps1 -SkipBuild
```

> **Nota (Windows):** si la politica de ejecucion de PowerShell del equipo
> bloquea scripts locales sin firmar (`... no se puede cargar porque la
> ejecucion de scripts esta deshabilitada ...`), ejecuta el script con:
> ```powershell
> powershell -NoProfile -ExecutionPolicy Bypass -File .\infra\docker-compose-smoke-test.ps1
> ```
> Esto aplica la politica solo al proceso del script, sin alterar la
> configuracion permanente del sistema.

El stack usa puertos de host aislados (15432/15433/15672/25672/18081/18082)
para no chocar con una instancia de desarrollo ya corriendo en los puertos por
defecto (5432/5433/5672/15672/8081/8082); el script restaura las variables de
entorno originales al terminar, incluso si una aseveracion falla.

## Pruebas de aceptacion (Cucumber)

El modulo `qa-automation` contiene una suite de pruebas de aceptacion BDD
(Cucumber, en espanol) que ejercita las APIs REST reales de la plataforma. Es un
modulo Maven independiente (no forma parte del reactor raiz) para que `mvn test`
en la raiz nunca dependa de Docker. Con el stack levantado (ver seccion
anterior):

```powershell
mvn -f qa-automation/pom.xml verify `
  "-Dcustomer.service.base-url=http://localhost:18081" `
  "-Dgeo.catalog.service.base-url=http://localhost:18082"
```

Detalle completo (prerequisitos, escenarios cubiertos, reportes) en
[`qa-automation/README.md`](qa-automation/README.md). La justificacion de esta
capa dentro de la estrategia global de pruebas esta en
[`docs/architecture/06-estrategia-de-pruebas.md`](docs/architecture/06-estrategia-de-pruebas.md).

## Development quickstart

Two common ways to run services locally:

1) Lightweight (recommended for iterative development)

- Run each service with the `local` Spring profile. This profile uses H2 in-memory,
  disables Flyway and disables the outbound outbox/ Rabbit health checks so
  you don't need Postgres or RabbitMQ.

Example (PowerShell):

```powershell
cd C:\git\smart-logistic\services\customer-service
mvn -DskipTests package
Start-Process -NoNewWindow -FilePath java -ArgumentList '-jar','target\customer-service.jar','--spring.profiles.active=local','--server.port=18081' -PassThru

cd C:\git\smart-logistic\services\geo-catalog-service
mvn -DskipTests package
Start-Process -NoNewWindow -FilePath java -ArgumentList '-jar','target\geo-catalog-service.jar','--spring.profiles.active=local','--server.port=18082' -PassThru
```

2) Full stack (integration / E2E)

- Use Docker Compose to start Postgres and RabbitMQ (see `infra/docker-compose.yml`).

Example (PowerShell):

```powershell
docker compose -f infra\docker-compose.yml up -d

cd services\customer-service
mvn -DskipTests package
Start-Process -NoNewWindow -FilePath java -ArgumentList '-jar','target\customer-service.jar' -PassThru

cd ..\geo-catalog-service
mvn -DskipTests package
Start-Process -NoNewWindow -FilePath java -ArgumentList '-jar','target\geo-catalog-service.jar' -PassThru
```

Notes:
- If `mvn package` fails with a repackage error, make sure no running Java process is holding the jar file (kill any previously started java processes from this repo).
- Use `--server.port=XXXX` to avoid port conflicts when running multiple instances.
- The `local` profile is only for developer convenience; don't use it in CI or production.