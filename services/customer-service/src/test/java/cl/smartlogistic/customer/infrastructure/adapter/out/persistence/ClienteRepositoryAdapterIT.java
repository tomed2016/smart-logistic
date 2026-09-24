package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.Coordenadas;
import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.Correo;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;
import cl.smartlogistic.customer.domain.model.Region;
import cl.smartlogistic.customer.domain.model.Telefono;
import cl.smartlogistic.customer.domain.model.TipoCliente;
import cl.smartlogistic.customer.domain.port.out.ClienteRepository;
import cl.smartlogistic.shared.rut.Rut;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica el ciclo completo de persistencia del agregado {@link Cliente} contra un
 * Postgres real (Testcontainers), incluyendo la migracion Flyway {@code V1__init_schema.sql}
 * y el mapeo bidireccional dominio/JPA realizado por {@link ClienteMapper}.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ClienteRepositoryAdapter.class)
@Testcontainers
class ClienteRepositoryAdapterIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private ClienteRepository clienteRepository;

    private static final Comuna PUENTE_ALTO = new Comuna("PUENTE_ALTO", "Puente Alto", Region.METROPOLITANA_DE_SANTIAGO);

    @Test
    @DisplayName("Guardar y recuperar un cliente con direccion preserva todos sus datos")
    void guardarYRecuperarCliente() {
        Cliente cliente = Cliente.crear(
                Rut.of("12345678-5"), TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of(new Telefono("+56912345678")), List.of(new Correo("juan@example.com")),
                CondicionPago.CONTADO, PrioridadComercial.ESTANDAR, "admin", Instant.now());
        cliente.agregarDireccion("Los Aromos", "123", PUENTE_ALTO, new Coordenadas(-33.6, -70.6),
                "Casa azul", true, "admin", Instant.now());
        cliente.eventosPendientes();

        clienteRepository.guardar(cliente);
        Optional<Cliente> recuperado = clienteRepository.buscarPorId(cliente.id());

        assertThat(recuperado).isPresent();
        Cliente encontrado = recuperado.get();
        assertThat(encontrado.rut()).isEqualTo(cliente.rut());
        assertThat(encontrado.nombre()).isEqualTo("Juan Perez");
        assertThat(encontrado.direcciones()).hasSize(1);
        assertThat(encontrado.direcciones().get(0).comuna().codigo()).isEqualTo("PUENTE_ALTO");
        assertThat(encontrado.direccionPrincipal()).isPresent();
    }

    @Test
    @DisplayName("existePorRut detecta correctamente RUTs ya persistidos")
    void existePorRutDetectaDuplicados() {
        Rut rut = Rut.of("7897867-8");
        Cliente cliente = Cliente.crear(
                rut, TipoCliente.EMPRESA, "Comercial Agua Ltda",
                List.of(new Telefono("+56922222222")), List.of(),
                CondicionPago.CREDITO_30_DIAS, PrioridadComercial.PREFERENTE, "admin", Instant.now());
        cliente.eventosPendientes();
        clienteRepository.guardar(cliente);

        assertThat(clienteRepository.existePorRut(rut)).isTrue();
        assertThat(clienteRepository.existePorRut(Rut.of("22.222.222-2"))).isFalse();
    }

    @Test
    @DisplayName("buscar() filtra por estado y pagina resultados")
    void buscarFiltraYPagina() {
        for (int i = 0; i < 3; i++) {
            Rut rut = Rut.of(rutValido(i));
            Cliente cliente = Cliente.crear(
                    rut, TipoCliente.PERSONA_NATURAL, "Cliente " + i,
                    List.of(new Telefono("+5691111111" + i)), List.of(),
                    CondicionPago.CONTADO, PrioridadComercial.ESTANDAR, "admin", Instant.now());
            cliente.eventosPendientes();
            clienteRepository.guardar(cliente);
        }

        ClienteRepository.PaginaClientes pagina = clienteRepository.buscar(
                new ClienteRepository.FiltroClientes(null, "ACTIVO"), 0, 2);

        assertThat(pagina.totalElementos()).isEqualTo(3);
        assertThat(pagina.contenido()).hasSize(2);
    }

    /** Genera RUTs validos de prueba variando el cuerpo numerico base. */
    private static String rutValido(int offset) {
        long[] cuerpos = {12345678L, 7897867L, 6000000L};
        long cuerpo = cuerpos[offset];
        return cuerpo + digitoVerificadorConocido(offset);
    }

    private static String digitoVerificadorConocido(int offset) {
        String[] dv = {"-5", "-8", "-K"};
        return dv[offset];
    }
}
