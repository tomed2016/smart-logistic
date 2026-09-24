package cl.smartlogistic.geo.application;

import cl.smartlogistic.geo.domain.exception.EntidadGeograficaNoEncontradaException;
import cl.smartlogistic.geo.domain.model.Comuna;
import cl.smartlogistic.geo.domain.model.Provincia;
import cl.smartlogistic.geo.domain.model.Region;
import cl.smartlogistic.geo.domain.port.out.ComunaRepositoryPort;
import cl.smartlogistic.geo.domain.port.out.ProvinciaRepositoryPort;
import cl.smartlogistic.geo.domain.port.out.RegionRepositoryPort;
import cl.smartlogistic.shared.calendar.CalendarioChileno;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoCatalogApplicationServiceTest {

    @Mock
    private RegionRepositoryPort regionRepository;
    @Mock
    private ProvinciaRepositoryPort provinciaRepository;
    @Mock
    private ComunaRepositoryPort comunaRepository;

    private final CalendarioChileno calendarioChileno = new CalendarioChileno();

    private GeoCatalogApplicationService crearServicio() {
        return new GeoCatalogApplicationService(regionRepository, provinciaRepository, comunaRepository, calendarioChileno);
    }

    @Test
    void listarRegionesDelegaEnElRepositorio() {
        when(regionRepository.listarTodas()).thenReturn(List.of(new Region(13, "Metropolitana de Santiago")));

        List<Region> resultado = crearServicio().listarRegiones();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Metropolitana de Santiago");
    }

    @Test
    void buscarRegionPorCodigoLanzaExcepcionSiNoExiste() {
        when(regionRepository.buscarPorCodigo(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crearServicio().buscarRegionPorCodigo(99))
                .isInstanceOf(EntidadGeograficaNoEncontradaException.class);
    }

    @Test
    void listarProvinciasDeRegionValidaQueLaRegionExista() {
        when(regionRepository.buscarPorCodigo(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crearServicio().listarProvinciasDeRegion(99))
                .isInstanceOf(EntidadGeograficaNoEncontradaException.class);
    }

    @Test
    void listarProvinciasDeRegionRetornaProvinciasSiLaRegionExiste() {
        when(regionRepository.buscarPorCodigo(13)).thenReturn(Optional.of(new Region(13, "Metropolitana")));
        when(provinciaRepository.listarPorRegion(13)).thenReturn(List.of(new Provincia(131, "Santiago", 13)));

        List<Provincia> resultado = crearServicio().listarProvinciasDeRegion(13);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarComunaPorCodigoLanzaExcepcionSiNoExiste() {
        when(comunaRepository.buscarPorCodigo(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crearServicio().buscarComunaPorCodigo(99999))
                .isInstanceOf(EntidadGeograficaNoEncontradaException.class);
    }

    @Test
    void buscarComunaPorCodigoRetornaComunaSiExiste() {
        Comuna maipu = new Comuna(13119, "Maipú", 131, 13);
        when(comunaRepository.buscarPorCodigo(13119)).thenReturn(Optional.of(maipu));

        Comuna resultado = crearServicio().buscarComunaPorCodigo(13119);

        assertThat(resultado.nombre()).isEqualTo("Maipú");
    }

    @Test
    void feriadosDelAnioDelegaEnCalendarioChileno() {
        List<cl.smartlogistic.shared.calendar.Feriado> feriados = crearServicio().feriadosDelAnio(2025);

        assertThat(feriados).isNotEmpty();
        assertThat(feriados).anyMatch(f -> f.fecha().equals(LocalDate.of(2025, 1, 1)));
    }

    @Test
    void esDiaHabilDelegaEnCalendarioChileno() {
        boolean anioNuevoEsHabil = crearServicio().esDiaHabil(LocalDate.of(2025, 1, 1));

        assertThat(anioNuevoEsHabil).isFalse();
    }
}
