package cl.smartlogistic.qa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * URL base de {@code geo-catalog-service} bajo prueba.
 *
 * <p>Vinculada a la propiedad {@code geo.catalog.service.base-url}. Ver
 * {@link CustomerServiceProperties} para el orden de precedencia de configuracion.</p>
 */
@ConfigurationProperties(prefix = "geo.catalog.service")
public record GeoCatalogServiceProperties(String baseUrl) {
}
