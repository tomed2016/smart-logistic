package cl.smartlogistic.customer.domain.port.out;

import cl.smartlogistic.shared.event.DomainEvent;

import java.util.List;

/**
 * Puerto de salida para publicar eventos de dominio. La implementacion en
 * infraestructura escribe en la tabla {@code outbox_event} dentro de la misma
 * transaccion del cambio de estado (patron Transactional Outbox, ver ADR-002); un
 * proceso separado se encarga de publicarlos efectivamente a RabbitMQ.
 */
public interface EventPublisherPort {

    void publicar(List<DomainEvent> eventos);
}
