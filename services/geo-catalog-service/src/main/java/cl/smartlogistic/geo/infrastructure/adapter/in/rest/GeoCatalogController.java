package cl.smartlogistic.geo.infrastructure.adapter.in.rest;

import cl.smartlogistic.geo.domain.port.in.ConsultarCatalogoGeograficoUseCase;
import cl.smartlogistic.geo.domain.port.in.ConsultarFeriadosUseCase;
import cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto.ComunaResponse;
import cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto.DiaHabilResponse;
import cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto.FeriadoResponse;
import cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto.ProvinciaResponse;
import cl.smartlogistic.geo.infrastructure.adapter.in.rest.dto.RegionResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Adaptador de entrada REST del servicio de referencia Catalogo Geografico CL.
 * Todos los endpoints son de solo lectura (ver {@code ConsultarCatalogoGeograficoUseCase}
 * y {@code ConsultarFeriadosUseCase}).
 */
@RestController
@RequestMapping("/api/v1")
public class GeoCatalogController {

    private final ConsultarCatalogoGeograficoUseCase catalogoUseCase;
    private final ConsultarFeriadosUseCase feriadosUseCase;

    public GeoCatalogController(ConsultarCatalogoGeograficoUseCase catalogoUseCase,
                                 ConsultarFeriadosUseCase feriadosUseCase) {
        this.catalogoUseCase = catalogoUseCase;
        this.feriadosUseCase = feriadosUseCase;
    }

    @GetMapping("/regiones")
    public List<RegionResponse> listarRegiones() {
        return catalogoUseCase.listarRegiones().stream().map(RegionResponse::from).toList();
    }

    @GetMapping("/regiones/{codigo}")
    public RegionResponse buscarRegion(@PathVariable int codigo) {
        return RegionResponse.from(catalogoUseCase.buscarRegionPorCodigo(codigo));
    }

    @GetMapping("/regiones/{codigo}/provincias")
    public List<ProvinciaResponse> listarProvinciasDeRegion(@PathVariable int codigo) {
        return catalogoUseCase.listarProvinciasDeRegion(codigo).stream().map(ProvinciaResponse::from).toList();
    }

    @GetMapping("/regiones/{codigo}/comunas")
    public List<ComunaResponse> listarComunasDeRegion(@PathVariable int codigo) {
        return catalogoUseCase.listarComunasDeRegion(codigo).stream().map(ComunaResponse::from).toList();
    }

    @GetMapping("/provincias/{codigo}/comunas")
    public List<ComunaResponse> listarComunasDeProvincia(@PathVariable int codigo) {
        return catalogoUseCase.listarComunasDeProvincia(codigo).stream().map(ComunaResponse::from).toList();
    }

    @GetMapping("/comunas")
    public List<ComunaResponse> listarComunas(@RequestParam(required = false) String nombre) {
        List<cl.smartlogistic.geo.domain.model.Comuna> comunas = (nombre == null || nombre.isBlank())
                ? catalogoUseCase.listarTodasLasComunas()
                : catalogoUseCase.buscarComunasPorNombre(nombre);
        return comunas.stream().map(ComunaResponse::from).toList();
    }

    @GetMapping("/comunas/{codigo}")
    public ComunaResponse buscarComuna(@PathVariable int codigo) {
        return ComunaResponse.from(catalogoUseCase.buscarComunaPorCodigo(codigo));
    }

    @GetMapping("/feriados")
    public List<FeriadoResponse> feriadosDelAnio(@RequestParam int anio) {
        return feriadosUseCase.feriadosDelAnio(anio).stream().map(FeriadoResponse::from).toList();
    }

    @GetMapping("/dias-habiles/verificar")
    public DiaHabilResponse verificarDiaHabil(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return new DiaHabilResponse(fecha, feriadosUseCase.esDiaHabil(fecha));
    }

    @GetMapping("/dias-habiles/siguiente")
    public DiaHabilResponse siguienteDiaHabil(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde) {
        LocalDate siguiente = feriadosUseCase.siguienteDiaHabil(desde);
        return new DiaHabilResponse(siguiente, true);
    }
}
