package cl.smartlogistic.geo.domain.port.out;

import cl.smartlogistic.geo.domain.model.Comuna;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (repositorio) para el catalogo de comunas. Solo lectura (ver
 * {@link RegionRepositoryPort}).
 */
public interface ComunaRepositoryPort {

    List<Comuna> listarPorProvincia(int provinciaCodigo);

    List<Comuna> listarPorRegion(int regionCodigo);

    List<Comuna> buscarPorNombre(String nombreParcial);

    Optional<Comuna> buscarPorCodigo(int codigo);

    List<Comuna> listarTodas();
}
