package cl.smartlogistic.customer.domain.event;

import cl.smartlogistic.shared.event.DomainEvent;

/** Publicado cuando se actualizan los datos de contacto/comerciales de un cliente. */
public final class ClienteActualizadoEvent extends DomainEvent {

    public ClienteActualizadoEvent(String clienteId) {
        super(clienteId, 1);
    }

    @Override
    public String eventType() {
        return "cliente.actualizado.v1";
    }
}
