package cl.smartlogistic.geo.domain.port.out;

import cl.smartlogistic.geo.domain.model.Region;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (repositorio) para el catalogo de regiones. Solo lectura: el
 * catalogo se carga via migraciones Flyway versionadas (fuente autoritativa
 * documentada en {@code docs/architecture/04-bounded-context-catalogo-geografico.md}),
 * no se administra en tiempo de ejecucion.
 */
public interface RegionRepositoryPort {

    List<Region> listarTodas();

    Optional<Region> buscarPorCodigo(int codigo);
}
