package cl.smartlogistic.customer.infrastructure.adapter.out.geocatalog;

import cl.smartlogistic.customer.domain.exception.ComunaCatalogNoDisponibleException;
import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Prueba unitaria del mapeo/logica pura del adaptador (parseo de codigo, mapeo de
 * respuesta a dominio, distincion 404 vs. error de servidor).
 *
 * <p><b>Alcance deliberadamente NO cubierto aqui</b>: el comportamiento real de
 * apertura de circuito y reintentos de Resilience4j depende del proxy AOP que Spring
 * construye alrededor del bean gestionado por el contenedor; invocar el metodo
 * directamente sobre una instancia creada a mano (como se hace en este test) NO pasa
 * por ese proxy. Por eso aqui se prueba el metodo de fallback
 * ({@code buscarPorCodigoFallback}) de forma aislada y explicita, en vez de simular
 * falsamente un circuito abierto. La integracion completa (proxy + configuracion de
 * {@code application.yml}) se valida al levantar el servicio contra
 * infra/docker-compose.yml.</p>
 */
class GeoCatalogHttpComunaCatalogAdapterTest {

    private static final String BASE_URL = "http://geo-catalog-test";

    private MockRestServiceServer servidorMock;
    private GeoCatalogHttpComunaCatalogAdapter adapter;

    private void construirAdapterConServidorMock() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        servidorMock = MockRestServiceServer.bindTo(builder).build();
        adapter = new GeoCatalogHttpComunaCatalogAdapter(builder.build());
    }

    @Test
    @DisplayName("Comuna encontrada se mapea correctamente al dominio, incluyendo la region")
    void buscarPorCodigoMapeaRespuestaExitosa() {
        construirAdapterConServidorMock();
        servidorMock.expect(requestTo(BASE_URL + "/api/v1/comunas/13119"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(
                        """
                        {"codigo": 13119, "nombre": "Maipú", "provinciaCodigo": 131, "regionCodigo": 13}
                        """,
                        MediaType.APPLICATION_JSON));

        Optional<Comuna> resultado = adapter.buscarPorCodigo("13119");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().codigo()).isEqualTo("13119");
        assertThat(resultado.get().nombre()).isEqualTo("Maipú");
        assertThat(resultado.get().region()).isEqualTo(Region.METROPOLITANA_DE_SANTIAGO);
        servidorMock.verify();
    }

    @Test
    @DisplayName("Comuna inexistente (404) se traduce en Optional.empty(), no en una falla")
    void buscarPorCodigoRetornaVacioCuandoElCatalogoRespondeNotFound() {
        construirAdapterConServidorMock();
        servidorMock.expect(requestTo(BASE_URL + "/api/v1/comunas/99999"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        Optional<Comuna> resultado = adapter.buscarPorCodigo("99999");

        assertThat(resultado).isEmpty();
        servidorMock.verify();
    }

    @Test
    @DisplayName("Codigo no numerico se resuelve como no encontrado sin llamar al catalogo")
    void buscarPorCodigoConCodigoNoNumericoNoConsultaElServicio() {
        construirAdapterConServidorMock();
        // No se registra ninguna expectativa en servidorMock: si el adaptador
        // intentara llamar por HTTP con un codigo invalido, la verificacion fallaria.

        Optional<Comuna> resultado = adapter.buscarPorCodigo("PUENTE_ALTO");

        assertThat(resultado).isEmpty();
        servidorMock.verify();
    }

    @Test
    @DisplayName("Un error de servidor (5xx) propaga la excepcion HTTP sin ser tratado como 404")
    void buscarPorCodigoPropagaErroresDeServidor() {
        construirAdapterConServidorMock();
        servidorMock.expect(requestTo(BASE_URL + "/api/v1/comunas/13119"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> adapter.buscarPorCodigo("13119"))
                .isInstanceOf(HttpServerErrorException.class);
        servidorMock.verify();
    }

    @Test
    @DisplayName("El fallback de resiliencia lanza ComunaCatalogNoDisponibleException, no oculta la falla")
    void fallbackLanzaExcepcionDeCatalogoNoDisponible() {
        construirAdapterConServidorMock();
        RuntimeException causaOriginal = new RuntimeException("circuito abierto");

        assertThatThrownBy(() -> adapter.buscarPorCodigoFallback("13119", causaOriginal))
                .isInstanceOf(ComunaCatalogNoDisponibleException.class)
                .hasMessageContaining("13119")
                .hasCause(causaOriginal);
    }
}
