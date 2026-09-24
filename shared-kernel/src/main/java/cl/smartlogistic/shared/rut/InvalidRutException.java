package cl.smartlogistic.shared.rut;

import cl.smartlogistic.shared.exception.DomainException;

/**
 * Se lanza cuando un RUT no cumple el formato chileno o su digito verificador es
 * incorrecto.
 */
public final class InvalidRutException extends DomainException {

    private static final String MESSAGE_TEMPLATE = "RUT invalido '%s': %s";

    public InvalidRutException(String rutOriginal, String razon) {
        super(String.format(MESSAGE_TEMPLATE, rutOriginal, razon));
    }
}
