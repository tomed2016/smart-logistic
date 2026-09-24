package cl.smartlogistic.qa.config;

import cl.smartlogistic.qa.QaAutomationTestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Punto de union entre Cucumber y Spring: cucumber-spring detecta esta clase (via
 * {@link CucumberContextConfiguration}) y usa la configuracion {@code @SpringBootTest}
 * para levantar (una unica vez, reutilizado entre escenarios) el
 * {@link org.springframework.context.ApplicationContext} desde el que se inyectan los
 * step definitions.
 *
 * <p>{@code webEnvironment = NONE}: este modulo no arranca un servidor embebido, solo
 * actua como cliente HTTP de los servicios reales (customer-service,
 * geo-catalog-service) que deben estar corriendo de forma independiente (ver
 * README.md).</p>
 */
@CucumberContextConfiguration
@SpringBootTest(classes = QaAutomationTestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CucumberSpringConfiguration {
}
