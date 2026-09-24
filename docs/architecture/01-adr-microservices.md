# ADR-001: Estilo arquitectónico — Microservicios desde el día 1

## Estado
Aceptado (decisión del stakeholder de negocio, 2026-09-23).

## Contexto
El sistema debe cubrir múltiples dominios de negocio con ciclos de cambio y escalado
distintos (Clientes cambia poco; Planificación Logística y Pedidos cambian mucho y
tienen picos de carga distintos — ej. la noche previa a cada día hábil). Se requiere
escalar progresivamente y desplegar en una nube aún no definida, sobre Docker.

## Decisión
Se adopta una arquitectura de **microservicios** con un servicio Spring Boot por
bounded context (ver `00-overview.md` para el mapa de contexto), en lugar de un
monolito modular.

## Justificación
- **Escalado independiente**: Planificación Logística (cálculo de rutas, CPU-intensivo)
  y Pedidos (picos de escritura) tienen perfiles de carga muy distintos a Clientes o
  Catálogo Geográfico; escalarlos por separado evita sobre-aprovisionar todo el sistema.
- **Ciclo de vida independiente**: nuevas reglas de reprogramación o recurrencia no
  deben forzar el redeploy de Clientes o Inventario.
- **Aislamiento de fallos**: una falla en el motor de ruteo no debe impedir registrar
  pedidos o consultar clientes (resiliencia).
- **Preparación multi-nube y multi-cliente futuro (Android/iOS)**: cada servicio expone
  API versionada y puede desplegarse en cualquier orquestador de contenedores
  (Kubernetes gestionado en cualquier proveedor), sin acoplarse a servicios
  propietarios de una nube específica en esta etapa.
- **Costo aceptado**: se asume la complejidad operacional adicional (observabilidad
  distribuida, consistencia eventual, *Database per Service*) porque el negocio ya
  proyecta crecimiento y múltiples equipos/clientes a futuro.

## Consecuencias
- Se requiere **Database per Service**: sin transacciones distribuidas ACID entre
  contextos; se usa consistencia eventual + patrón *Transactional Outbox* para publicar
  eventos de forma confiable.
- Se requiere versionado explícito de contratos (REST y eventos) desde el inicio.
- Se requiere observabilidad distribuida (trazas correlacionadas) desde el inicio, no
  como añadido posterior.
- Cada servicio mantiene su propio esquema de migraciones (Flyway) y su propio
  Dockerfile, permitiendo despliegue y rollback independientes.
- Un *API Gateway* (a introducir cuando exista más de un cliente externo/canal) centraliza
  autenticación y enrutamiento hacia los servicios internos.

## Alternativas descartadas
- **Monolito modular**: menor complejidad operacional inicial, pero el stakeholder
  requiere explícitamente escalado independiente y separación de despliegue desde el
  inicio; se descarta para evitar una migración costosa posterior.
