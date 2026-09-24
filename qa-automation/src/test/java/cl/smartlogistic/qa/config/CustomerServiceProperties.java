package cl.smartlogistic.qa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * URL base de {@code customer-service} bajo prueba.
 *
 * <p>Vinculada a la propiedad {@code customer.service.base-url}, configurable via
 * {@code application.yml}, variable de entorno ({@code CUSTOMER_SERVICE_BASE_URL}) o
 * propiedad de sistema ({@code -Dcustomer.service.base-url=...}), en ese orden de
 * precedencia estandar de Spring Boot. Ver {@code README.md} de este modulo.</p>
 */
@ConfigurationProperties(prefix = "customer.service")
public record CustomerServiceProperties(String baseUrl) {
}
