package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

public final class DireccionNoEncontradaException extends DomainException {
    public DireccionNoEncontradaException(String direccionId) {
        super("No existe una direccion con id '%s' para este cliente".formatted(direccionId));
    }
}
