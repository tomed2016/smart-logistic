package cl.smartlogistic.customer.domain.exception;

import cl.smartlogistic.shared.exception.DomainException;

/** Se lanza cuando se referencia un codigo de comuna que no existe en el catalogo. */
public final class ComunaDesconocidaException extends DomainException {
    public ComunaDesconocidaException(String codigoComuna) {
        super("La comuna con codigo '%s' no existe en el catalogo geografico".formatted(codigoComuna));
    }
}
