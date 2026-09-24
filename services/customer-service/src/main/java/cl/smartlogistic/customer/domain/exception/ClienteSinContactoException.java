package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

/**
 * Invariante de negocio: un cliente {@code ACTIVO} debe tener al menos un telefono o
 * un correo de contacto.
 */
public final class ClienteSinContactoException extends DomainException {
    public ClienteSinContactoException() {
        super("Un cliente activo debe tener al menos un telefono o un correo de contacto");
    }
}
