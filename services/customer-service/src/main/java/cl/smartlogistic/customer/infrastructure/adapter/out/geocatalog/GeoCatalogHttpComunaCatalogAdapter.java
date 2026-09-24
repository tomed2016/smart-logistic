package cl.smartlogistic.customer.infrastructure.adapter.out.geocatalog;

import cl.smartlogistic.customer.domain.exception.ComunaCatalogNoDisponibleException;
import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.Region;
import cl.smartlogistic.customer.domain.port.out.ComunaCatalogPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Adaptador de {@link ComunaCatalogPort} que consulta el catalogo geografico
 * oficial en {@code geo-catalog-service} vía HTTP sincrono ({@link RestClient}).
 *
 * <p>Resiliencia (ver ADR 05): las llamadas estan protegidas por un
 * {@code @Retry} (reintentos ante fallas transitorias de red) envuelto por un
 * {@code @CircuitBreaker} (evita saturar un geo-catalog-service caido con reintentos
 * indefinidos; tras superar el umbral de fallas, el circuito se abre y las llamadas
 * fallan rapido durante la ventana configurada). Ademas, las respuestas exitosas se
 * cachean en memoria ({@code @Cacheable}) porque el catalogo geografico cambia con
 * una frecuencia practicamente nula (solo via migraciones Flyway), por lo que servir
 * desde cache no compromete la consistencia y reduce tanto la latencia como el
 * impacto de caidas breves del servicio de catalogo.</p>
 *
 * <p>Distincion importante: un 404 del catalogo (comuna inexistente) se traduce en
 * {@code Optional.empty()} — es una respuesta valida del dominio, NO una falla. Solo
 * las fallas de infraestructura (timeout, conexion rechazada, 5xx, circuito abierto)
 * activan el metodo de fallback, que lanza {@link ComunaCatalogNoDisponibleException}
 * en vez de enmascararse silenciosamente como "comuna no encontrada".</p>
 */
@Component
@Profile("!local")
public class GeoCatalogHttpComunaCatalogAdapter implements ComunaCatalogPort {

    private static final Logger log = LoggerFactory.getLogger(GeoCatalogHttpComunaCatalogAdapter.class);
    private static final String RESILIENCE_INSTANCE = "geoCatalogService";

    private final RestClient geoCatalogRestClient;

    public GeoCatalogHttpComunaCatalogAdapter(RestClient geoCatalogRestClient) {
        this.geoCatalogRestClient = geoCatalogRestClient;
    }

    // Nota: Spring Cache desenvuelve automaticamente los metodos que retornan
    // Optional<T> (desde Spring 4.3): el valor evaluado por "unless" es el
    // contenido desenvuelto (un Comuna) o null cuando el Optional esta vacio -
    // nunca el propio Optional. Por eso la condicion solo compara contra null;
    // usar "#result.isEmpty()" fallaria en tiempo de ejecucion (Comuna no tiene
    // ese metodo) y de hecho lo hacia hasta este fix.
    @Override
    @Cacheable(cacheNames = "comunas", unless = "#result == null")
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "buscarPorCodigoFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    public Optional<Comuna> buscarPorCodigo(String codigo) {
        int codigoIne;
        try {
            codigoIne = Integer.parseInt(codigo.trim());
        } catch (NumberFormatException e) {
            // Un codigo no numerico nunca existira en el catalogo INE: es un caso de
            // "no encontrado" del dominio, no una falla de infraestructura a reintentar.
            return Optional.empty();
        }

        try {
            ComunaCatalogoResponse respuesta = geoCatalogRestClient.get()
                    .uri("/api/v1/comunas/{codigo}", codigoIne)
                    .retrieve()
                    .body(ComunaCatalogoResponse.class);
            return Optional.ofNullable(respuesta).map(this::aDominio);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    /**
     * Metodo de fallback invocado por Resilience4j cuando el circuito esta abierto o
     * se agotaron los reintentos configurados. Firma exigida por el framework: mismos
     * parametros que el metodo protegido, mas la excepcion causante al final.
     *
     * <p>Visibilidad de paquete (no {@code private}) deliberada para permitir probar
     * esta logica de forma aislada en {@code GeoCatalogHttpComunaCatalogAdapterTest}
     * sin necesidad de levantar el contexto completo de Spring + Resilience4j AOP.</p>
     */
    Optional<Comuna> buscarPorCodigoFallback(String codigo, Throwable causa) {
        log.warn("geo-catalog-service no disponible al resolver comuna '{}': {}", codigo, causa.toString());
        throw new ComunaCatalogNoDisponibleException(codigo, causa);
    }

    private Comuna aDominio(ComunaCatalogoResponse respuesta) {
        Region region = Region.porCodigoIne(respuesta.regionCodigo());
        return new Comuna(String.valueOf(respuesta.codigo()), respuesta.nombre(), region);
    }
}
