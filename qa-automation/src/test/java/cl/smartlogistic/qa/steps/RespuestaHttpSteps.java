package cl.smartlogistic.qa.steps;

import cl.smartlogistic.qa.support.EscenarioContexto;
import io.cucumber.java.es.Entonces;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions genéricos y reutilizables entre bounded contexts: aserciones sobre
 * el código de estado HTTP de la última llamada realizada en el escenario.
 *
 * <p>Se centraliza aquí para evitar que {@link ClienteSteps} y
 * {@link CatalogoGeograficoSteps} definan el mismo texto Gherkin dos veces, lo que
 * Cucumber rechaza como "step definitions duplicadas".</p>
 */
public class RespuestaHttpSteps {

    private final EscenarioContexto contexto;

    public RespuestaHttpSteps(EscenarioContexto contexto) {
        this.contexto = contexto;
    }

    @Entonces("la respuesta tiene el estado HTTP {int}")
    public void laRespuestaTieneElEstadoHttp(int estadoEsperado) {
        assertThat(contexto.ultimoEstadoHttp().value()).isEqualTo(estadoEsperado);
    }
}
