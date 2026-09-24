package cl.smartlogistic.customer.infrastructure.adapter.out.messaging;

import cl.smartlogistic.customer.domain.port.out.EventPublisherPort;
import cl.smartlogistic.shared.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Adaptador de {@link EventPublisherPort} que implementa el lado "escritor" del
 * patron Transactional Outbox: serializa cada evento y lo inserta en la tabla
 * {@code outbox_event} dentro de la misma transaccion JPA que el cambio de estado
 * del agregado (no publica directamente a RabbitMQ; ver {@link OutboxDispatcher}).
 */
@Component
class OutboxEventPublisherAdapter implements EventPublisherPort {

    /**
     * ObjectMapper dedicado, configurado para serializar por campos (no por
     * getters), ya que los eventos de dominio usan estilo "fluido" (ej.
     * {@code rut()} en vez de {@code getRut()}) que Jackson no reconoce por
     * defecto. Es independiente del ObjectMapper de Spring MVC usado para las
     * respuestas REST.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    OutboxEventPublisherAdapter(OutboxEventJpaRepository outboxEventJpaRepository) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
    }

    @Override
    public void publicar(List<DomainEvent> eventos) {
        for (DomainEvent evento : eventos) {
            String payload = serializar(evento);
            OutboxEventJpaEntity entidad = new OutboxEventJpaEntity(
                    UUID.randomUUID(),
                    evento.aggregateId(),
                    evento.eventType(),
                    evento.schemaVersion(),
                    payload,
                    Instant.now());
            outboxEventJpaRepository.save(entidad);
        }
    }

    private String serializar(DomainEvent evento) {
        try {
            return OBJECT_MAPPER.writeValueAsString(evento);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo serializar el evento " + evento.eventType(), e);
        }
    }
}
