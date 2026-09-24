package cl.smartlogistic.customer.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Habilita la abstraccion de cache de Spring, usada por
 * {@code GeoCatalogHttpComunaCatalogAdapter} para memorizar respuestas del catalogo
 * geografico (dato de referencia practicamente inmutable). El proveedor concreto
 * (Caffeine) y sus parametros (tamano maximo, expiracion) se configuran en
 * {@code application.yml} bajo {@code spring.cache.caffeine.spec}.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
