package cl.smartlogistic.qa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

/**
 * Clientes HTTP de bajo nivel hacia los servicios bajo prueba.
 *
 * <p>Se desactiva el manejo de errores por defecto de {@link RestClient} (que lanza
 * {@code RestClientResponseException} ante 4xx/5xx) mediante un
 * {@code defaultStatusHandler} sin efecto: los escenarios de aceptacion necesitan poder
 * inspeccionar respuestas de error (codigo, cuerpo) igual que las respuestas exitosas,
 * sin depender de manejo de excepciones en los step definitions.</p>
 */
@Configuration
public class RestClientsConfig {

    @Bean
    public RestClient customerServiceRestClient(CustomerServiceProperties properties) {
        return construir(properties.baseUrl());
    }

    @Bean
    public RestClient geoCatalogServiceRestClient(GeoCatalogServiceProperties properties) {
        return construir(properties.baseUrl());
    }

    private RestClient construir(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    // No-op: se delega en los step definitions la aserción sobre el
                    // codigo de estado y el cuerpo de la respuesta, incluyendo los
                    // escenarios negativos (400/404/409/503).
                })
                .build();
    }
}
