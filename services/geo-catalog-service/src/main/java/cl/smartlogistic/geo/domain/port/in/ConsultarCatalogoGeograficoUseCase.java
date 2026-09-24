package cl.smartlogistic.geo.domain.port.in;

import cl.smartlogistic.geo.domain.model.Comuna;
import cl.smartlogistic.geo.domain.model.Provincia;
import cl.smartlogistic.geo.domain.model.Region;

import java.util.List;

/**
 * Casos de uso de consulta del catalogo geografico (regiones, provincias, comunas).
 * Servicio de referencia de solo lectura: no expone comandos de creacion/edicion en
 * esta iteracion porque el catalogo se administra via migraciones versionadas.
 */
public interface ConsultarCatalogoGeograficoUseCase {

    List<Region> listarRegiones();

    Region buscarRegionPorCodigo(int codigo);

    List<Provincia> listarProvinciasDeRegion(int regionCodigo);

    List<Comuna> listarComunasDeProvincia(int provinciaCodigo);

    List<Comuna> listarComunasDeRegion(int regionCodigo);

    List<Comuna> buscarComunasPorNombre(String nombreParcial);

    Comuna buscarComunaPorCodigo(int codigo);

    List<Comuna> listarTodasLasComunas();
}
