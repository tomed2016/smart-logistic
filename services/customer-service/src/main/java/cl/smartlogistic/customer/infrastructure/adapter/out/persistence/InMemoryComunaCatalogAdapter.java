package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.Region;
import cl.smartlogistic.customer.domain.port.out.ComunaCatalogPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador de {@link ComunaCatalogPort} que carga un catalogo geografico embebido
 * (CSV en el classpath) a memoria al iniciar el servicio.
 *
 * <p><b>Solo activo bajo el perfil {@code local}</b> (desarrollo/pruebas manuales sin
 * un geo-catalog-service en ejecucion). En el resto de los perfiles, el adaptador
 * real es
 * {@code cl.smartlogistic.customer.infrastructure.adapter.out.geocatalog.GeoCatalogHttpComunaCatalogAdapter},
 * que consulta el catalogo oficial de 346 comunas. Ver
 * docs/architecture/05-integracion-clientes-catalogo-geografico.md.</p>
 *
 * <p><b>Advertencia de compatibilidad</b>: los codigos de este catalogo embebido son
 * slugs internos (ej. {@code PUENTE_ALTO}), <u>no</u> codigos oficiales INE. Una
 * direccion creada bajo el perfil {@code local} con un codigo de este catalogo no
 * sera resoluble si luego se consulta bajo otro perfil contra geo-catalog-service (y
 * viceversa). Esto es aceptable porque {@code local} es exclusivamente un modo de
 * desarrollo desconectado, nunca un entorno con datos que deban persistir entre
 * perfiles.</p>
 */
@Component
@Profile("local")
public class InMemoryComunaCatalogAdapter implements ComunaCatalogPort {

    private static final String RECURSO_CSV = "catalogo/comunas-chile.csv";

    private final Map<String, Comuna> comunasPorCodigo = new ConcurrentHashMap<>();

    public InMemoryComunaCatalogAdapter() {
        cargarCatalogo();
    }

    private void cargarCatalogo() {
        ClassPathResource recurso = new ClassPathResource(RECURSO_CSV);
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(recurso.getInputStream(), StandardCharsets.UTF_8))) {
            String linea = lector.readLine(); // encabezado
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split(",", 3);
                String codigo = campos[0].trim();
                String nombre = campos[1].trim();
                Region region = Region.valueOf(campos[2].trim());
                comunasPorCodigo.put(codigo, new Comuna(codigo, nombre, region));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el catalogo de comunas: " + RECURSO_CSV, e);
        }
    }

    @Override
    public Optional<Comuna> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(comunasPorCodigo.get(codigo));
    }
}
