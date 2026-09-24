package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

/** Se lanza al intentar registrar un cliente con un RUT ya existente en el sistema. */
public final class RutDuplicadoException extends DomainException {
    public RutDuplicadoException(String rutFormateado) {
        super("Ya existe un cliente registrado con el RUT '%s'".formatted(rutFormateado));
    }
}
