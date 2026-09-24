package cl.smartlogistic.customer.infrastructure.adapter.out.geocatalog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configura el cliente HTTP hacia {@code geo-catalog-service}. Solo se activa fuera
 * del perfil {@code local} (ver
 * {@code cl.smartlogistic.customer.infrastructure.adapter.out.persistence.InMemoryComunaCatalogAdapter},
 * que cubre el desarrollo/pruebas manuales sin depender de un geo-catalog-service en
 * ejecucion).
 */
@Configuration
@Profile("!local")
@EnableConfigurationProperties(GeoCatalogClientConfig.GeoCatalogServiceProperties.class)
public class GeoCatalogClientConfig {

    @Bean
    public RestClient geoCatalogRestClient(GeoCatalogServiceProperties propiedades) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(propiedades.connectTimeout())
                .withReadTimeout(propiedades.readTimeout());
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        return RestClient.builder()
                .baseUrl(propiedades.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Propiedades de conexion hacia geo-catalog-service, con valores por defecto
     * pensados para desarrollo local (fuera de Docker Compose, donde el nombre de
     * host es el nombre del servicio, ver infra/docker-compose.yml).
     */
    @ConfigurationProperties(prefix = "geo-catalog-service")
    public record GeoCatalogServiceProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {

        public GeoCatalogServiceProperties {
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = "http://localhost:8082";
            }
            if (connectTimeout == null) {
                connectTimeout = Duration.ofSeconds(2);
            }
            if (readTimeout == null) {
                readTimeout = Duration.ofSeconds(3);
            }
        }
    }
}
