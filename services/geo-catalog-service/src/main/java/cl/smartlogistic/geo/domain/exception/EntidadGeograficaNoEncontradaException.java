package cl.smartlogistic.geo.domain.exception;

/**
 * Se lanza cuando se consulta una comuna, provincia o region por un codigo que no
 * existe en el catalogo oficial cargado.
 */
public class EntidadGeograficaNoEncontradaException extends RuntimeException {
    private static final String MESSAGE = "%s con codigo %d no encontrada en el catalogo";

    public EntidadGeograficaNoEncontradaException(String tipo, int codigo) {
        super(String.format(MESSAGE, tipo, codigo));
    }
}
