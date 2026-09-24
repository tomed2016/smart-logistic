package cl.smartlogistic.customer.domain.port.out;

import cl.smartlogistic.customer.domain.model.Comuna;

import java.util.Optional;

/**
 * Puerto de salida hacia el catalogo geografico chileno (comunas/regiones). En la
 * Iteracion 1 se implementa con un catalogo embebido en memoria; en la Iteracion 2
 * se reemplazara por un cliente HTTP al servicio de Catalogo Geografico compartido
 * sin que el dominio de Clientes deba cambiar.
 */
public interface ComunaCatalogPort {

    Optional<Comuna> buscarPorCodigo(String codigo);
}
