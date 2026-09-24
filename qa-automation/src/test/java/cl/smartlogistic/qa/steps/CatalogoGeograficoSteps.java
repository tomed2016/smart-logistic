package cl.smartlogistic.qa.steps;

import cl.smartlogistic.qa.support.EscenarioContexto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions del Catálogo Geográfico CL: consulta regiones, comunas y días
 * hábiles contra la API REST real de {@code geo-catalog-service} (ver
 * {@code GeoCatalogController}).
 */
public class CatalogoGeograficoSteps {

    private final RestClient geoCatalogServiceRestClient;
    private final EscenarioContexto contexto;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CatalogoGeograficoSteps(@Qualifier("geoCatalogServiceRestClient") RestClient geoCatalogServiceRestClient,
                                    EscenarioContexto contexto) {
        this.geoCatalogServiceRestClient = geoCatalogServiceRestClient;
        this.contexto = contexto;
    }

    @Cuando("consulto la región con código {int}")
    public void consultoLaRegionConCodigo(int codigo) {
        ejecutar("/api/v1/regiones/{codigo}", codigo);
    }

    @Cuando("listo las comunas de la región con código {int}")
    public void listoLasComunasDeLaRegionConCodigo(int codigo) {
        ejecutar("/api/v1/regiones/{codigo}/comunas", codigo);
    }

    @Cuando("consulto la comuna con código {int}")
    public void consultoLaComunaConCodigo(int codigo) {
        ejecutar("/api/v1/comunas/{codigo}", codigo);
    }

    @Cuando("verifico si la fecha {string} es día hábil")
    public void verificoSiLaFechaEsDiaHabil(String fechaIso) {
        var respuesta = geoCatalogServiceRestClient.get()
                .uri("/api/v1/dias-habiles/verificar?fecha={fecha}", fechaIso)
                .retrieve()
                .toEntity(String.class);
        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
    }

    private void ejecutar(String uriTemplate, int codigo) {
        var respuesta = geoCatalogServiceRestClient.get()
                .uri(uriTemplate, codigo)
                .retrieve()
                .toEntity(String.class);
        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
    }

    @Y("la región consultada tiene nombre {string}")
    public void laRegionConsultadaTieneNombre(String nombreEsperado) {
        assertThat(leerCampoTexto("nombre")).isEqualTo(nombreEsperado);
    }

    @Y("el listado de comunas incluye la comuna {string}")
    public void elListadoDeComunasIncluyeLaComuna(String nombreComuna) {
        JsonNode comunas = leerNodo(contexto.ultimoCuerpoRespuesta());
        boolean incluida = false;
        for (JsonNode comuna : comunas) {
            if (nombreComuna.equals(comuna.path("nombre").asText())) {
                incluida = true;
                break;
            }
        }
        assertThat(incluida)
                .withFailMessage("Se esperaba encontrar la comuna '%s' en la respuesta: %s",
                        nombreComuna, contexto.ultimoCuerpoRespuesta())
                .isTrue();
    }

    @Y("la comuna consultada tiene nombre {string}")
    public void laComunaConsultadaTieneNombre(String nombreEsperado) {
        assertThat(leerCampoTexto("nombre")).isEqualTo(nombreEsperado);
    }

    @Y("la comuna consultada pertenece a la región con código {int}")
    public void laComunaConsultadaPerteneceALaRegionConCodigo(int codigoRegionEsperado) {
        assertThat(leerNodo(contexto.ultimoCuerpoRespuesta()).path("regionCodigo").asInt())
                .isEqualTo(codigoRegionEsperado);
    }

    @Y("la fecha consultada no es día hábil")
    public void laFechaConsultadaNoEsDiaHabil() {
        assertThat(leerNodo(contexto.ultimoCuerpoRespuesta()).path("esDiaHabil").asBoolean()).isFalse();
    }

    private String leerCampoTexto(String nombreCampo) {
        return leerNodo(contexto.ultimoCuerpoRespuesta()).path(nombreCampo).asText();
    }

    private JsonNode leerNodo(String cuerpoJson) {
        try {
            return objectMapper.readTree(cuerpoJson);
        } catch (Exception e) {
            throw new IllegalStateException("La respuesta no es un JSON valido: " + cuerpoJson, e);
        }
    }
}
