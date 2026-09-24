package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.Region;
import cl.smartlogistic.customer.domain.port.out.ComunaCatalogPort;
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
 * (CSV en el classpath) a memoria al iniciar el servicio. Ver limitaciones de
 * alcance documentadas en {@link Comuna}.
 */
@Component
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
