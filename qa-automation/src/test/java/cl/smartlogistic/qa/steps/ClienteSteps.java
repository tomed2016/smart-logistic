package cl.smartlogistic.qa.steps;

import cl.smartlogistic.qa.support.EscenarioContexto;
import cl.smartlogistic.qa.support.GeneradorDeRut;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions del bounded context Clientes: crea, consulta, actualiza y desactiva
 * clientes contra la API REST real de {@code customer-service} (ver
 * {@code ClienteController}).
 */
public class ClienteSteps {

    private final RestClient customerServiceRestClient;
    private final EscenarioContexto contexto;
    private final GeneradorDeRut generadorDeRut;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClienteSteps(@Qualifier("customerServiceRestClient") RestClient customerServiceRestClient,
                         EscenarioContexto contexto,
                         GeneradorDeRut generadorDeRut) {
        this.customerServiceRestClient = customerServiceRestClient;
        this.contexto = contexto;
        this.generadorDeRut = generadorDeRut;
    }

    @Dado("que he creado un cliente persona natural con nombre {string}")
    @Cuando("creo un cliente persona natural con nombre {string}")
    public void creoUnClientePersonaNatural(String nombre) {
        String rut = generadorDeRut.generarRutValido();
        contexto.registrarId("rutClienteCreado", rut);
        crearCliente(rut, nombre);
    }

    @Cuando("intento crear otro cliente con el mismo RUT y nombre {string}")
    public void intentoCrearOtroClienteConElMismoRut(String nombre) {
        String rutYaRegistrado = contexto.obtenerId("rutClienteCreado");
        crearCliente(rutYaRegistrado, nombre);
    }

    private void crearCliente(String rut, String nombre) {
        String cuerpo = """
                {
                  "rut": "%s",
                  "tipoCliente": "PERSONA_NATURAL",
                  "nombre": "%s",
                  "telefonos": ["+56912345678"],
                  "correos": ["qa@smartlogistic.cl"],
                  "condicionPago": "CONTADO",
                  "prioridadComercial": "ESTANDAR"
                }
                """.formatted(rut, nombre);

        var respuesta = customerServiceRestClient.post()
                .uri("/api/v1/clientes")
                .header("Content-Type", "application/json")
                .body(cuerpo)
                .retrieve()
                .toEntity(String.class);

        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
        if (respuesta.getStatusCode().is2xxSuccessful()) {
            contexto.registrarId("clienteId", leerCampo(respuesta.getBody(), "id"));
        }
    }

    @Cuando("consulto el cliente creado")
    public void consultoElClienteCreado() {
        consultarClientePorId(contexto.obtenerId("clienteId"));
    }

    @Cuando("consulto el cliente con identificador {string}")
    public void consultoElClienteConIdentificador(String clienteId) {
        consultarClientePorId(clienteId);
    }

    private void consultarClientePorId(String clienteId) {
        var respuesta = customerServiceRestClient.get()
                .uri("/api/v1/clientes/{id}", clienteId)
                .retrieve()
                .toEntity(String.class);
        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
    }

    @Cuando("agrego al cliente creado una dirección en la comuna {string}")
    public void agregoAlClienteCreadoUnaDireccion(String codigoComuna) {
        String clienteId = contexto.obtenerId("clienteId");
        String cuerpo = """
                {
                  "calle": "Av. QA Automation",
                  "numero": "1234",
                  "codigoComuna": "%s",
                  "latitud": -33.5,
                  "longitud": -70.75,
                  "referencia": "Escenario de prueba automatizado",
                  "marcarComoPrincipal": true
                }
                """.formatted(codigoComuna);

        var respuesta = customerServiceRestClient.post()
                .uri("/api/v1/clientes/{id}/direcciones", clienteId)
                .header("Content-Type", "application/json")
                .body(cuerpo)
                .retrieve()
                .toEntity(String.class);
        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
    }

    @Cuando("actualizo el nombre del cliente creado a {string}")
    public void actualizoElNombreDelClienteCreado(String nuevoNombre) {
        String clienteId = contexto.obtenerId("clienteId");
        String cuerpo = """
                {
                  "nombre": "%s",
                  "telefonos": ["+56912345678"],
                  "correos": ["qa@smartlogistic.cl"],
                  "condicionPago": "CONTADO",
                  "prioridadComercial": "ESTANDAR"
                }
                """.formatted(nuevoNombre);

        var respuesta = customerServiceRestClient.put()
                .uri("/api/v1/clientes/{id}", clienteId)
                .header("Content-Type", "application/json")
                .body(cuerpo)
                .retrieve()
                .toEntity(String.class);
        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
    }

    @Cuando("desactivo el cliente creado")
    public void desactivoElClienteCreado() {
        String clienteId = contexto.obtenerId("clienteId");
        var respuesta = customerServiceRestClient.delete()
                .uri("/api/v1/clientes/{id}", clienteId)
                .retrieve()
                .toEntity(String.class);
        contexto.registrarRespuesta(respuesta.getStatusCode(), respuesta.getBody());
    }

    @Y("el cliente creado tiene nombre {string}")
    @Y("el cliente consultado tiene nombre {string}")
    public void elClienteTieneNombre(String nombreEsperado) {
        assertThat(leerCampo(contexto.ultimoCuerpoRespuesta(), "nombre")).isEqualTo(nombreEsperado);
    }

    @Y("el cliente creado tiene estado {string}")
    @Y("el cliente consultado tiene estado {string}")
    public void elClienteTieneEstado(String estadoEsperado) {
        assertThat(leerCampo(contexto.ultimoCuerpoRespuesta(), "estado")).isEqualTo(estadoEsperado);
    }

    @Y("el cliente creado tiene condición de pago {string}")
    @Y("el cliente consultado tiene condición de pago {string}")
    public void elClienteTieneCondicionDePago(String condicionEsperada) {
        assertThat(leerCampo(contexto.ultimoCuerpoRespuesta(), "condicionPago")).isEqualTo(condicionEsperada);
    }

    @Y("el cliente creado tiene prioridad comercial {string}")
    @Y("el cliente consultado tiene prioridad comercial {string}")
    public void elClienteTienePrioridadComercial(String prioridadEsperada) {
        assertThat(leerCampo(contexto.ultimoCuerpoRespuesta(), "prioridadComercial")).isEqualTo(prioridadEsperada);
    }

    @Y("el cliente consultado tiene exactamente {int} dirección")
    public void elClienteConsultadoTieneExactamenteDirecciones(int cantidadEsperada) {
        JsonNode direcciones = leerNodo(contexto.ultimoCuerpoRespuesta()).path("direcciones");
        assertThat(direcciones.size()).isEqualTo(cantidadEsperada);
    }

    @Y("la primera dirección del cliente consultado tiene comuna {string}")
    public void laPrimeraDireccionDelClienteConsultadoTieneComuna(String comunaEsperada) {
        JsonNode direcciones = leerNodo(contexto.ultimoCuerpoRespuesta()).path("direcciones");
        assertThat(direcciones.get(0).path("comunaNombre").asText()).isEqualTo(comunaEsperada);
    }

    private String leerCampo(String cuerpoJson, String nombreCampo) {
        return leerNodo(cuerpoJson).path(nombreCampo).asText();
    }

    private JsonNode leerNodo(String cuerpoJson) {
        try {
            return objectMapper.readTree(cuerpoJson);
        } catch (Exception e) {
            throw new IllegalStateException("La respuesta no es un JSON valido: " + cuerpoJson, e);
        }
    }
}
