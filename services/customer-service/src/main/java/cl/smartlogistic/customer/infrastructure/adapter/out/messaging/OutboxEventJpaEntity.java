package cl.smartlogistic.customer.infrastructure.adapter.out.messaging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa una fila de la tabla {@code outbox_event} (patron Transactional
 * Outbox, ver ADR-002). Se persiste en la misma transaccion que el cambio de estado
 * del agregado; un publicador asincrono separado la envia a RabbitMQ y completa
 * {@code publishedAt}.
 */
@Entity
@Table(name = "outbox_event")
public class OutboxEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "occurred_on", nullable = false)
    private Instant occurredOn;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected OutboxEventJpaEntity() {
        // requerido por JPA
    }

    public OutboxEventJpaEntity(UUID id, String aggregateId, String eventType, int schemaVersion,
                                 String payload, Instant occurredOn) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.schemaVersion = schemaVersion;
        this.payload = payload;
        this.occurredOn = occurredOn;
    }

    public void marcarComoPublicado(Instant instante) {
        this.publishedAt = instante;
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
