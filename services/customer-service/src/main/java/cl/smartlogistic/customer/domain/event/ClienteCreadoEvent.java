package cl.smartlogistic.customer.domain.event;

import cl.smartlogistic.shared.event.DomainEvent;

/** Publicado cuando se registra un cliente nuevo. */
public final class ClienteCreadoEvent extends DomainEvent {

    private final String rut;

    public ClienteCreadoEvent(String clienteId, String rut) {
        super(clienteId, 1);
        this.rut = rut;
    }

    @Override
    public String eventType() {
        return "cliente.creado.v1";
    }

    public String rut() {
        return rut;
    }
}
