package cl.smartlogistic.geo.application;

import cl.smartlogistic.geo.domain.exception.EntidadGeograficaNoEncontradaException;
import cl.smartlogistic.geo.domain.model.Comuna;
import cl.smartlogistic.geo.domain.model.Provincia;
import cl.smartlogistic.geo.domain.model.Region;
import cl.smartlogistic.geo.domain.port.in.ConsultarCatalogoGeograficoUseCase;
import cl.smartlogistic.geo.domain.port.in.ConsultarFeriadosUseCase;
import cl.smartlogistic.geo.domain.port.out.ComunaRepositoryPort;
import cl.smartlogistic.geo.domain.port.out.ProvinciaRepositoryPort;
import cl.smartlogistic.geo.domain.port.out.RegionRepositoryPort;
import cl.smartlogistic.shared.calendar.CalendarioChileno;
import cl.smartlogistic.shared.calendar.Feriado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de aplicacion del bounded context Catalogo Geografico. Orquesta los
 * puertos de salida (repositorios de solo lectura) y la logica de calendario del
 * shared-kernel; no contiene reglas de negocio propias mas alla de validar
 * existencia (catalogo de referencia).
 */
@Service
@Transactional(readOnly = true)
public class GeoCatalogApplicationService implements ConsultarCatalogoGeograficoUseCase, ConsultarFeriadosUseCase {

    private final RegionRepositoryPort regionRepository;
    private final ProvinciaRepositoryPort provinciaRepository;
    private final ComunaRepositoryPort comunaRepository;
    private final CalendarioChileno calendarioChileno;

    public GeoCatalogApplicationService(RegionRepositoryPort regionRepository,
                                         ProvinciaRepositoryPort provinciaRepository,
                                         ComunaRepositoryPort comunaRepository,
                                         CalendarioChileno calendarioChileno) {
        this.regionRepository = regionRepository;
        this.provinciaRepository = provinciaRepository;
        this.comunaRepository = comunaRepository;
        this.calendarioChileno = calendarioChileno;
    }

    @Override
    public List<Region> listarRegiones() {
        return regionRepository.listarTodas();
    }

    @Override
    public Region buscarRegionPorCodigo(int codigo) {
        return regionRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new EntidadGeograficaNoEncontradaException("Region", codigo));
    }

    @Override
    public List<Provincia> listarProvinciasDeRegion(int regionCodigo) {
        buscarRegionPorCodigo(regionCodigo);
        return provinciaRepository.listarPorRegion(regionCodigo);
    }

    @Override
    public List<Comuna> listarComunasDeProvincia(int provinciaCodigo) {
        if (provinciaRepository.buscarPorCodigo(provinciaCodigo).isEmpty()) {
            throw new EntidadGeograficaNoEncontradaException("Provincia", provinciaCodigo);
        }
        return comunaRepository.listarPorProvincia(provinciaCodigo);
    }

    @Override
    public List<Comuna> listarComunasDeRegion(int regionCodigo) {
        buscarRegionPorCodigo(regionCodigo);
        return comunaRepository.listarPorRegion(regionCodigo);
    }

    @Override
    public List<Comuna> buscarComunasPorNombre(String nombreParcial) {
        return comunaRepository.buscarPorNombre(nombreParcial);
    }

    @Override
    public Comuna buscarComunaPorCodigo(int codigo) {
        return comunaRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new EntidadGeograficaNoEncontradaException("Comuna", codigo));
    }

    @Override
    public List<Comuna> listarTodasLasComunas() {
        return comunaRepository.listarTodas();
    }

    @Override
    public List<Feriado> feriadosDelAnio(int anio) {
        return calendarioChileno.feriadosDelAnio(anio);
    }

    @Override
    public boolean esDiaHabil(LocalDate fecha) {
        return calendarioChileno.esDiaHabil(fecha);
    }

    @Override
    public LocalDate siguienteDiaHabil(LocalDate desde) {
        return calendarioChileno.siguienteDiaHabil(desde);
    }
}
