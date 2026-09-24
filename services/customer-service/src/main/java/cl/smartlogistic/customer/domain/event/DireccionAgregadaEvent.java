package cl.smartlogistic.customer.domain.event;

import cl.smartlogistic.shared.event.DomainEvent;

/** Publicado cuando se agrega una nueva direccion a un cliente. */
public final class DireccionAgregadaEvent extends DomainEvent {

    private final String direccionId;

    public DireccionAgregadaEvent(String clienteId, String direccionId) {
        super(clienteId, 1);
        this.direccionId = direccionId;
    }

    @Override
    public String eventType() {
        return "cliente.direccion-agregada.v1";
    }

    public String direccionId() {
        return direccionId;
    }
}
