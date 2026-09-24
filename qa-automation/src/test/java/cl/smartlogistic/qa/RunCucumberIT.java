package cl.smartlogistic.qa;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Punto de entrada JUnit 5 que ejecuta todos los archivos {@code .feature} de
 * {@code src/test/resources/features} usando el motor de Cucumber.
 *
 * <p>El nombre termina en {@code IT} (no {@code Test}) a proposito: maven-surefire esta
 * desactivado en este modulo y esta suite se ejecuta unicamente via
 * {@code maven-failsafe-plugin} en la fase {@code integration-test}, igual que las
 * pruebas {@code *IT} basadas en Testcontainers de los demas modulos. Ver pom.xml y
 * README.md de este modulo.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "cl.smartlogistic.qa")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report/index.html, json:target/cucumber-report/cucumber.json")
public class RunCucumberIT {
}
