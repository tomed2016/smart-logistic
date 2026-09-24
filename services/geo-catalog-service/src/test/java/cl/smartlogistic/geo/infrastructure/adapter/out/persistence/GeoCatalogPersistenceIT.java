package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import cl.smartlogistic.geo.domain.model.Comuna;
import cl.smartlogistic.geo.domain.model.Provincia;
import cl.smartlogistic.geo.domain.model.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que la migracion Flyway completa (esquema + seed de las 16 regiones,
 * 56 provincias y 346 comunas oficiales) se aplica correctamente contra un
 * Postgres real (Testcontainers) y que los adaptadores de persistencia mapean
 * correctamente hacia el modelo de dominio.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RegionRepositoryAdapter.class, ProvinciaRepositoryAdapter.class, ComunaRepositoryAdapter.class})
@Testcontainers
class GeoCatalogPersistenceIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private RegionRepositoryAdapter regionRepository;

    @Autowired
    private ProvinciaRepositoryAdapter provinciaRepository;

    @Autowired
    private ComunaRepositoryAdapter comunaRepository;

    @Test
    @DisplayName("El catalogo cargado via Flyway contiene las 16 regiones oficiales")
    void catalogoContieneLas16Regiones() {
        List<Region> regiones = regionRepository.listarTodas();

        assertThat(regiones).hasSize(16);
        assertThat(regiones).anyMatch(r -> r.codigo() == 13 && r.nombre().equals("Región Metropolitana de Santiago"));
    }

    @Test
    @DisplayName("El catalogo cargado via Flyway contiene las 346 comunas oficiales")
    void catalogoContieneLas346Comunas() {
        List<Comuna> comunas = comunaRepository.listarTodas();

        assertThat(comunas).hasSize(346);
    }

    @Test
    @DisplayName("Maipu se resuelve con su provincia y region correctas")
    void buscarComunaPorCodigoResuelveJerarquiaCompleta() {
        Optional<Comuna> maipu = comunaRepository.buscarPorCodigo(13119);

        assertThat(maipu).isPresent();
        assertThat(maipu.get().nombre()).isEqualTo("Maipú");
        assertThat(maipu.get().provinciaCodigo()).isEqualTo(131);
        assertThat(maipu.get().regionCodigo()).isEqualTo(13);
    }

    @Test
    @DisplayName("Listar comunas por region retorna solo comunas de esa region")
    void listarComunasPorRegionFiltraCorrectamente() {
        List<Comuna> comunasRM = comunaRepository.listarPorRegion(13);

        assertThat(comunasRM).isNotEmpty();
        assertThat(comunasRM).allMatch(c -> c.regionCodigo() == 13);
        assertThat(comunasRM).anyMatch(c -> c.nombre().equals("Santiago"));
    }

    @Test
    @DisplayName("Listar provincias por region retorna las provincias correctas")
    void listarProvinciasPorRegionFiltraCorrectamente() {
        List<Provincia> provinciasRM = provinciaRepository.listarPorRegion(13);

        assertThat(provinciasRM).hasSize(6);
    }
}
