package cl.smartlogistic.geo.domain.port.out;

import cl.smartlogistic.geo.domain.model.Provincia;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (repositorio) para el catalogo de provincias. Solo lectura (ver
 * {@link RegionRepositoryPort}).
 */
public interface ProvinciaRepositoryPort {

    List<Provincia> listarPorRegion(int regionCodigo);

    Optional<Provincia> buscarPorCodigo(int codigo);
}
