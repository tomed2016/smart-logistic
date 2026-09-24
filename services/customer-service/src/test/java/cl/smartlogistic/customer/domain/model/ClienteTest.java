package cl.smartlogistic.customer.domain.model;

import cl.smartlogistic.customer.domain.event.ClienteActualizadoEvent;
import cl.smartlogistic.customer.domain.event.ClienteCreadoEvent;
import cl.smartlogistic.customer.domain.event.ClienteDesactivadoEvent;
import cl.smartlogistic.customer.domain.event.DireccionAgregadaEvent;
import cl.smartlogistic.customer.domain.exception.ClienteSinContactoException;
import cl.smartlogistic.customer.domain.exception.DireccionNoEncontradaException;
import cl.smartlogistic.shared.rut.Rut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClienteTest {

    private static final Instant AHORA = Instant.parse("2024-01-01T12:00:00Z");
    private static final Comuna PUENTE_ALTO = new Comuna("PUENTE_ALTO", "Puente Alto", Region.METROPOLITANA_DE_SANTIAGO);
    private static final Comuna PROVIDENCIA = new Comuna("PROVIDENCIA", "Providencia", Region.METROPOLITANA_DE_SANTIAGO);

    @Test
    @DisplayName("Crear cliente con contacto valido registra evento ClienteCreadoEvent")
    void crearClienteConContactoValido() {
        Cliente cliente = clienteDePrueba();

        assertThat(cliente.estado()).isEqualTo(EstadoCliente.ACTIVO);
        assertThat(cliente.eventosPendientes())
                .hasSize(1)
                .first()
                .isInstanceOf(ClienteCreadoEvent.class);
    }

    @Test
    @DisplayName("No permite crear un cliente sin telefono ni correo")
    void rechazaClienteSinContacto() {
        assertThatThrownBy(() -> Cliente.crear(
                Rut.of("12345678-5"), TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of(), List.of(), CondicionPago.CONTADO, PrioridadComercial.ESTANDAR,
                "admin", AHORA))
                .isInstanceOf(ClienteSinContactoException.class);
    }

    @Test
    @DisplayName("eventosPendientes() limpia la lista tras ser leida")
    void eventosPendientesSeLimpianTrasLeerlos() {
        Cliente cliente = clienteDePrueba();
        cliente.eventosPendientes();

        assertThat(cliente.eventosPendientes()).isEmpty();
    }

    @Test
    @DisplayName("Actualizar datos de contacto registra ClienteActualizadoEvent")
    void actualizarDatosDeContacto() {
        Cliente cliente = clienteDePrueba();
        cliente.eventosPendientes();

        cliente.actualizarDatosDeContacto("Juan Perez Soto", List.of(new Telefono("+56911111111")),
                List.of(), CondicionPago.CREDITO_30_DIAS, PrioridadComercial.VIP, "admin", AHORA);

        assertThat(cliente.nombre()).isEqualTo("Juan Perez Soto");
        assertThat(cliente.condicionPago()).isEqualTo(CondicionPago.CREDITO_30_DIAS);
        assertThat(cliente.eventosPendientes()).first().isInstanceOf(ClienteActualizadoEvent.class);
    }

    @Test
    @DisplayName("Rechaza actualizar dejando al cliente sin ningun contacto")
    void rechazaActualizacionSinContacto() {
        Cliente cliente = clienteDePrueba();

        assertThatThrownBy(() -> cliente.actualizarDatosDeContacto("Juan Perez", List.of(), List.of(),
                CondicionPago.CONTADO, PrioridadComercial.ESTANDAR, "admin", AHORA))
                .isInstanceOf(ClienteSinContactoException.class);
    }

    @Test
    @DisplayName("La primera direccion agregada se marca principal automaticamente")
    void primeraDireccionEsPrincipalAutomaticamente() {
        Cliente cliente = clienteDePrueba();

        DireccionId id = cliente.agregarDireccion("Los Aromos", "123", PUENTE_ALTO,
                new Coordenadas(-33.6, -70.6), "Casa azul", false, "admin", AHORA);

        assertThat(cliente.direccionPrincipal()).isPresent();
        assertThat(cliente.direccionPrincipal().get().id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Marcar una nueva direccion como principal desmarca la anterior")
    void agregarDireccionPrincipalDesmarcaAnterior() {
        Cliente cliente = clienteDePrueba();
        cliente.agregarDireccion("Los Aromos", "123", PUENTE_ALTO,
                new Coordenadas(-33.6, -70.6), "Casa azul", false, "admin", AHORA);

        DireccionId nuevaPrincipal = cliente.agregarDireccion("Av. Providencia", "1000", PROVIDENCIA,
                new Coordenadas(-33.42, -70.61), "Oficina 501", true, "admin", AHORA);

        assertThat(cliente.direcciones()).hasSize(2);
        assertThat(cliente.direccionPrincipal()).isPresent();
        assertThat(cliente.direccionPrincipal().get().id()).isEqualTo(nuevaPrincipal);
    }

    @Test
    @DisplayName("Actualizar una direccion inexistente lanza DireccionNoEncontradaException")
    void actualizarDireccionInexistente() {
        Cliente cliente = clienteDePrueba();
        DireccionId idInexistente = DireccionId.nuevo();

        assertThatThrownBy(() -> cliente.actualizarDireccion(idInexistente, "Calle", "1", PUENTE_ALTO,
                new Coordenadas(0, 0), null, "admin", AHORA))
                .isInstanceOf(DireccionNoEncontradaException.class);
    }

    @Test
    @DisplayName("Agregar una direccion registra DireccionAgregadaEvent")
    void agregarDireccionRegistraEvento() {
        Cliente cliente = clienteDePrueba();
        cliente.eventosPendientes();

        cliente.agregarDireccion("Los Aromos", "123", PUENTE_ALTO,
                new Coordenadas(-33.6, -70.6), null, false, "admin", AHORA);

        assertThat(cliente.eventosPendientes()).first().isInstanceOf(DireccionAgregadaEvent.class);
    }

    @Test
    @DisplayName("Desactivar un cliente cambia su estado y registra ClienteDesactivadoEvent")
    void desactivarCliente() {
        Cliente cliente = clienteDePrueba();
        cliente.eventosPendientes();

        cliente.desactivar("admin", AHORA);

        assertThat(cliente.estado()).isEqualTo(EstadoCliente.INACTIVO);
        assertThat(cliente.eventosPendientes()).first().isInstanceOf(ClienteDesactivadoEvent.class);
    }

    @Test
    @DisplayName("Reactivar un cliente sin contacto vigente falla")
    void reactivarSinContactoFalla() {
        Cliente sinContactoTrasReconstruir = Cliente.reconstruir(
                ClienteId.nuevo(), Rut.of("12345678-5"), TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of(), List.of(), CondicionPago.CONTADO, PrioridadComercial.ESTANDAR,
                EstadoCliente.INACTIVO, List.of(), cl.smartlogistic.shared.audit.AuditInfo.crear("admin", AHORA));

        assertThatThrownBy(() -> sinContactoTrasReconstruir.reactivar("admin", AHORA))
                .isInstanceOf(ClienteSinContactoException.class);
    }

    private static Cliente clienteDePrueba() {
        return Cliente.crear(
                Rut.of("12345678-5"),
                TipoCliente.PERSONA_NATURAL,
                "Juan Perez",
                List.of(new Telefono("+56912345678")),
                List.of(new Correo("juan.perez@example.com")),
                CondicionPago.CONTADO,
                PrioridadComercial.ESTANDAR,
                "admin",
                AHORA);
    }
}
