package cl.smartlogistic.geo.domain.exception;

/**
 * Se lanza cuando se consulta una comuna, provincia o region por un codigo que no
 * existe en el catalogo oficial cargado.
 */
public class EntidadGeograficaNoEncontradaException extends RuntimeException {

    public EntidadGeograficaNoEncontradaException(String tipo, int codigo) {
        super("%s con codigo %d no encontrada en el catalogo".formatted(tipo, codigo));
    }
}
