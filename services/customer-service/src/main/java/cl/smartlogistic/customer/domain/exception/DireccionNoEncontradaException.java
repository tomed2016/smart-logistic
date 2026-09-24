package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

public final class DireccionNoEncontradaException extends DomainException {
    private static final String MESSAGE = "No existe una direccion con id '%s' para este cliente";

    public DireccionNoEncontradaException(String direccionId) {
        super(String.format(MESSAGE, direccionId));
    }
}
