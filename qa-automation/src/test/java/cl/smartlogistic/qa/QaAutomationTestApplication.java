package cl.smartlogistic.qa;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Contexto Spring Boot minimo de este modulo de pruebas de aceptacion.
 *
 * <p>No expone servidor web propio ({@code webEnvironment = NONE} en
 * {@link cl.smartlogistic.qa.config.CucumberSpringConfiguration}): su unico proposito
 * es levantar un {@link org.springframework.context.ApplicationContext} de Spring Boot
 * para inyectar en los step definitions clientes HTTP ya configurados
 * ({@code RestClient}) y propiedades de configuracion (URLs base de los servicios bajo
 * prueba), reutilizando el mismo modelo de programacion (DI, {@code @ConfigurationProperties})
 * que el resto de la plataforma.</p>
 *
 * <p>{@code @ComponentScan} habilita que cucumber-spring re-escale automaticamente a
 * scope {@code cucumber-glue} (una instancia nueva por escenario) tanto los step
 * definitions como los beans de soporte compartido (ej. {@code EscenarioContexto}),
 * evitando fugas de estado entre escenarios.</p>
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("cl.smartlogistic.qa")
@ConfigurationPropertiesScan("cl.smartlogistic.qa.config")
public class QaAutomationTestApplication {
}
