package cl.smartlogistic.customer.domain.exception;

/**
 * Se lanza cuando el catalogo geografico compartido (geo-catalog-service) no puede
 * ser consultado (circuito abierto tras fallas repetidas, o reintentos agotados) y
 * no existe una respuesta cacheada previa para el codigo de comuna solicitado.
 *
 * <p>Se modela deliberadamente <b>separada</b> de {@link ComunaDesconocidaException}:
 * esta ultima significa "el codigo de comuna no existe" (error del cliente, HTTP 400),
 * mientras que esta excepcion significa "no pudimos verificar si el codigo existe
 * porque el servicio de catalogo esta temporalmente inalcanzable" (error transitorio
 * de infraestructura, HTTP 503). Confundir ambos casos llevaria a rechazar
 * incorrectamente direcciones validas solo porque el catalogo geografico esta caido.</p>
 */
public final class ComunaCatalogNoDisponibleException extends RuntimeException {
    private static final String MESSAGE = "El catalogo geografico no esta disponible actualmente; no fue posible verificar el codigo de comuna '%s'. Intente nuevamente en unos momentos.";

    public ComunaCatalogNoDisponibleException(String codigoComuna, Throwable causa) {
        super(String.format(MESSAGE, codigoComuna), causa);
    }
}
