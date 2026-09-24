package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

public final class ClienteNoEncontradoException extends DomainException {
    public ClienteNoEncontradoException(String clienteId) {
        super("No existe un cliente con id '%s'".formatted(clienteId));
    }
}
