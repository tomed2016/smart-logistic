# smart-logistic

Bootstrap inicial de una plataforma logística y administrativa para distribución de bidones de agua en Chile.

## Documentación clave

- `/docs/project-context.md`: supuestos, MVP y casos de uso prioritarios.
- `/docs/architecture.md`: arquitectura objetivo, C4, contexts y roadmap.
- `/docs/domain-glossary.md`: glosario de dominio inicial.
- `/docs/backlog.md`: backlog priorizado por iteraciones.
- `/docs/decisions`: ADRs iniciales.

## Stack del bootstrap

- Java 17 compatible con el runner actual, con ADR para evolucionar a Java 21.
- Spring Boot 3.3.
- Maven.
- Flyway.
- H2 para bootstrap local/tests y PostgreSQL para ejecución vía Docker Compose.

## Ejecución local

```bash
mvn spring-boot:run
```

## Pruebas

```bash
mvn test
```

## Docker Compose

```bash
docker compose up --build
```
