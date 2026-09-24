package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

public final class ClienteNoEncontradoException extends DomainException {
    private static final String MESSAGE = "No existe un cliente con id '%s'";

    public ClienteNoEncontradoException(String clienteId) {
        super(String.format(MESSAGE, clienteId));
    }
}
