package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

/** Se lanza al intentar registrar un cliente con un RUT ya existente en el sistema. */
public final class RutDuplicadoException extends DomainException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "Ya existe un cliente registrado con el RUT '%s'";

    public RutDuplicadoException(String rutFormateado) {
        super(String.format(MESSAGE, rutFormateado));
    }
}
