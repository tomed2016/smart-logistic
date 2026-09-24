package cl.smartlogistic.qa.support;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Estado compartido entre steps dentro de un mismo escenario ("World" en
 * terminologia Cucumber).
 *
 * <p>Con {@code @Scope("cucumber-glue")} cucumber-spring crea una instancia nueva por
 * escenario y la descarta al finalizar, evitando fugas de estado entre escenarios que
 * se ejecutan en el mismo proceso de Maven (ver
 * <a href="https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-spring">
 * documentacion de cucumber-spring</a>).</p>
 */
@Component
@Scope("cucumber-glue")
public class EscenarioContexto {

    private final Map<String, String> identificadores = new HashMap<>();

    private HttpStatusCode ultimoEstadoHttp;
    private String ultimoCuerpoRespuesta;

    /** Registra un identificador de negocio (ej. "clienteId", "direccionId") creado en el escenario. */
    public void registrarId(String clave, String valor) {
        identificadores.put(clave, valor);
    }

    /** Recupera un identificador previamente registrado en el mismo escenario. */
    public String obtenerId(String clave) {
        String valor = identificadores.get(clave);
        if (valor == null) {
            throw new IllegalStateException(
                    "No hay un identificador registrado bajo la clave '" + clave
                            + "'. Pasos previos del escenario deben registrarlo primero.");
        }
        return valor;
    }

    public void registrarRespuesta(HttpStatusCode estado, String cuerpo) {
        this.ultimoEstadoHttp = estado;
        this.ultimoCuerpoRespuesta = cuerpo;
    }

    public HttpStatusCode ultimoEstadoHttp() {
        requireRespuestaRegistrada();
        return ultimoEstadoHttp;
    }

    public String ultimoCuerpoRespuesta() {
        requireRespuestaRegistrada();
        return ultimoCuerpoRespuesta;
    }

    private void requireRespuestaRegistrada() {
        if (ultimoEstadoHttp == null) {
            throw new IllegalStateException(
                    "Aun no se ha ejecutado ninguna llamada HTTP en este escenario.");
        }
    }
}
