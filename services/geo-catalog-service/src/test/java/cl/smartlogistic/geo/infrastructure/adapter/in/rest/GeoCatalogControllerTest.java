package cl.smartlogistic.geo.infrastructure.adapter.in.rest;

import cl.smartlogistic.geo.domain.exception.EntidadGeograficaNoEncontradaException;
import cl.smartlogistic.geo.domain.model.Comuna;
import cl.smartlogistic.geo.domain.model.Provincia;
import cl.smartlogistic.geo.domain.model.Region;
import cl.smartlogistic.geo.domain.port.in.ConsultarCatalogoGeograficoUseCase;
import cl.smartlogistic.geo.domain.port.in.ConsultarFeriadosUseCase;
import cl.smartlogistic.shared.calendar.CalendarioChileno;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GeoCatalogController.class)
class GeoCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarCatalogoGeograficoUseCase catalogoUseCase;

    @MockBean
    private ConsultarFeriadosUseCase feriadosUseCase;

    @Test
    void listarRegionesRetorna200ConListado() throws Exception {
        when(catalogoUseCase.listarRegiones()).thenReturn(List.of(new Region(13, "Metropolitana de Santiago")));

        mockMvc.perform(get("/api/v1/regiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(13))
                .andExpect(jsonPath("$[0].nombre").value("Metropolitana de Santiago"));
    }

    @Test
    void buscarRegionInexistenteRetorna404() throws Exception {
        when(catalogoUseCase.buscarRegionPorCodigo(99)).thenThrow(new EntidadGeograficaNoEncontradaException("Region", 99));

        mockMvc.perform(get("/api/v1/regiones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listarProvinciasDeRegionRetorna200() throws Exception {
        when(catalogoUseCase.listarProvinciasDeRegion(13)).thenReturn(List.of(new Provincia(131, "Santiago", 13)));

        mockMvc.perform(get("/api/v1/regiones/13/provincias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(131));
    }

    @Test
    void buscarComunaRetorna200() throws Exception {
        when(catalogoUseCase.buscarComunaPorCodigo(13119)).thenReturn(new Comuna(13119, "Maipú", 131, 13));

        mockMvc.perform(get("/api/v1/comunas/13119"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maipú"));
    }

    @Test
    void buscarComunaInexistenteRetorna404() throws Exception {
        when(catalogoUseCase.buscarComunaPorCodigo(1)).thenThrow(new EntidadGeograficaNoEncontradaException("Comuna", 1));

        mockMvc.perform(get("/api/v1/comunas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarComunasPorNombreDelegaEnBusquedaCuandoSeEnviaParametro() throws Exception {
        when(catalogoUseCase.buscarComunasPorNombre("maip")).thenReturn(List.of(new Comuna(13119, "Maipú", 131, 13)));

        mockMvc.perform(get("/api/v1/comunas").param("nombre", "maip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(13119));
    }

    @Test
    void feriadosDelAnioRetorna200ConListado() throws Exception {
        CalendarioChileno calendario = new CalendarioChileno();
        when(feriadosUseCase.feriadosDelAnio(2025)).thenReturn(calendario.feriadosDelAnio(2025));

        mockMvc.perform(get("/api/v1/feriados").param("anio", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Año Nuevo"));
    }

    @Test
    void verificarDiaHabilRetorna200() throws Exception {
        LocalDate fecha = LocalDate.of(2025, 1, 1);
        when(feriadosUseCase.esDiaHabil(fecha)).thenReturn(false);

        mockMvc.perform(get("/api/v1/dias-habiles/verificar").param("fecha", "2025-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esDiaHabil").value(false));
    }
}
