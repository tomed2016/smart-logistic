package cl.smartlogistic.customer.domain.event;

import cl.smartlogistic.shared.event.DomainEvent;

/** Publicado cuando un cliente se desactiva (soft-delete logico, no elimina historial). */
public final class ClienteDesactivadoEvent extends DomainEvent {

    public ClienteDesactivadoEvent(String clienteId) {
        super(clienteId, 1);
    }

    @Override
    public String eventType() {
        return "cliente.desactivado.v1";
    }
}
