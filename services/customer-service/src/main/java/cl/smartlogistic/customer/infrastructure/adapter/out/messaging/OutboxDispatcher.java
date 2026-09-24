package cl.smartlogistic.customer.infrastructure.adapter.out.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Publicador asincrono del patron Transactional Outbox (ADR-002): periodicamente
 * lee los eventos pendientes de la tabla {@code outbox_event} y los envia a
 * RabbitMQ. Garantiza entrega <i>at-least-once</i>; los consumidores deben ser
 * idempotentes usando {@code eventId} (ver payload) para deduplicar.
 */
@Component
@ConditionalOnProperty(prefix = "smart-logistic.outbox", name = "enabled", havingValue = "true", matchIfMissing = true)
class OutboxDispatcher {

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    OutboxDispatcher(OutboxEventJpaRepository outboxEventJpaRepository,
                      RabbitTemplate rabbitTemplate,
                      RabbitMqProperties rabbitMqProperties) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = rabbitMqProperties.exchangeName();
    }

    @Scheduled(fixedDelayString = "${smart-logistic.outbox.dispatch-fixed-delay-ms:2000}")
    @Transactional
    void despacharPendientes() {
        List<OutboxEventJpaEntity> pendientes =
                outboxEventJpaRepository.findByPublishedAtIsNullOrderByOccurredOnAsc(PageRequest.of(0, 100));

        for (OutboxEventJpaEntity evento : pendientes) {
            rabbitTemplate.convertAndSend(exchangeName, evento.getEventType(), evento.getPayload());
            evento.marcarComoPublicado(Instant.now());
            outboxEventJpaRepository.save(evento);
        }
    }
}
