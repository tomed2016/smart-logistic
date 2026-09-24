package cl.smartlogistic.shared.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base para todos los eventos de dominio publicados entre microservicios (ver
 * ADR-002: mensajeria asincrona con RabbitMQ + patron Transactional Outbox).
 *
 * <p>Cada evento tiene un {@code eventId} unico que los consumidores deben usar para
 * deduplicar procesamiento, dado que la entrega es <i>at-least-once</i>.</p>
 */
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;
    private final String aggregateId;
    private final int schemaVersion;

    protected DomainEvent(String aggregateId, int schemaVersion) {
        this(UUID.randomUUID(), Instant.now(), aggregateId, schemaVersion);
    }

    protected DomainEvent(UUID eventId, Instant occurredOn, String aggregateId, int schemaVersion) {
        this.eventId = Objects.requireNonNull(eventId, "eventId no puede ser nulo");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn no puede ser nulo");
        this.aggregateId = Objects.requireNonNull(aggregateId, "aggregateId no puede ser nulo");
        this.schemaVersion = schemaVersion;
    }

    /** Nombre logico del evento usado como routing key (ej. {@code cliente.creado.v1}). */
    public abstract String eventType();

    public UUID eventId() {
        return eventId;
    }

    public Instant occurredOn() {
        return occurredOn;
    }

    public String aggregateId() {
        return aggregateId;
    }

    public int schemaVersion() {
        return schemaVersion;
    }
}
