package cl.smartlogistic.shared.rut;

import cl.smartlogistic.shared.exception.DomainException;

/**
 * Se lanza cuando un RUT no cumple el formato chileno o su digito verificador es
 * incorrecto.
 */
public final class InvalidRutException extends DomainException {

    public InvalidRutException(String rutOriginal, String razon) {
        super("RUT invalido '%s': %s".formatted(rutOriginal, razon));
    }
}
