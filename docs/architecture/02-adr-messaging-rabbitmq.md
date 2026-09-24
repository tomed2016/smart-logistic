# ADR-002: Mensajería asíncrona entre microservicios — RabbitMQ

## Estado
Aceptado (decisión del stakeholder de negocio, 2026-09-23).

## Contexto
Los bounded contexts (Clientes, Inventario, Pedidos, Recurrencias, Reprogramación,
Planificación Logística, Pagos) deben integrarse sin acoplamiento síncrono fuerte.
Ejemplos: cuando se confirma un pedido, Inventario debe reservar stock; cuando se
genera una ocurrencia recurrente, Pedidos debe crear un pedido nuevo; cuando se
planifica una ruta, Pedidos debe transicionar a `PLANIFICADO`.

## Decisión
Se usa **RabbitMQ** como *message broker* para eventos de dominio entre servicios,
con exchanges de tipo *topic* por bounded context (ej. `clientes.events`,
`pedidos.events`) y colas dedicadas por servicio consumidor (*competing consumers*
cuando aplique escalado horizontal del consumidor).

## Justificación
- El volumen inicial (una empresa distribuidora regional, planificación diaria) no
  requiere el throughput de streaming de Kafka; RabbitMQ ofrece semántica de colas con
  *routing* flexible y es significativamente más simple de operar y monitorear con un
  equipo pequeño.
- RabbitMQ soporta *dead-letter exchanges* nativos, útiles para reprocesar eventos de
  reprogramación/recurrencia fallidos sin build-out adicional.
- Es compatible con el patrón **Transactional Outbox** (tabla `outbox_event` +
  publicador que lee la tabla y publica a RabbitMQ), que se adopta para garantizar
  entrega *at-least-once* sin transacciones distribuidas.
- Si en el futuro el volumen de eventos crece (telemetría de flota, tracking en tiempo
  real de vehículos) se reevaluará Kafka para esos flujos específicos; la decisión no
  es irreversible gracias al aislamiento por *bounded context*.

## Consecuencias
- Los consumidores deben ser **idempotentes** (se garantiza *at-least-once*, no
  *exactly-once*): cada evento de dominio incluye `eventId` (UUID) único; el
  consumidor registra los `eventId` procesados (tabla `processed_event`) para
  deduplicar.
- Cada servicio productor implementa **Transactional Outbox**: el cambio de estado del
  agregado y el registro del evento en `outbox_event` se persisten en la misma
  transacción JPA; un publicador asíncrono (poller o `@TransactionalEventListener` +
  scheduler) los envía a RabbitMQ y marca `published_at`.
- Contratos de eventos versionados en JSON con `schemaVersion`, publicados y
  documentados junto al código de cada servicio (`docs/events/`).

## Alternativas descartadas
- **Apache Kafka**: mayor throughput y replay ilimitado, pero mayor complejidad
  operacional (Zookeeper/KRaft, particionamiento, retención) no justificada por el
  volumen actual del negocio.
