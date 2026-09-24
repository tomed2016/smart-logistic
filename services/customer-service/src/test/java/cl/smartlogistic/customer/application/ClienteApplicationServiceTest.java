package cl.smartlogistic.customer.application;

import cl.smartlogistic.customer.domain.exception.ClienteNoEncontradoException;
import cl.smartlogistic.customer.domain.exception.ComunaDesconocidaException;
import cl.smartlogistic.customer.domain.exception.RutDuplicadoException;
import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.DireccionId;
import cl.smartlogistic.customer.domain.model.EstadoCliente;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;
import cl.smartlogistic.customer.domain.model.Region;
import cl.smartlogistic.customer.domain.model.TipoCliente;
import cl.smartlogistic.customer.domain.port.in.AgregarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.CrearClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.DesactivarClienteUseCase;
import cl.smartlogistic.customer.domain.port.out.ClienteRepository;
import cl.smartlogistic.customer.domain.port.out.ComunaCatalogPort;
import cl.smartlogistic.customer.domain.port.out.EventPublisherPort;
import cl.smartlogistic.shared.rut.Rut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteApplicationServiceTest {

    private static final Comuna PUENTE_ALTO = new Comuna("PUENTE_ALTO", "Puente Alto", Region.METROPOLITANA_DE_SANTIAGO);

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private EventPublisherPort eventPublisherPort;
    @Mock
    private ComunaCatalogPort comunaCatalogPort;

    private ClienteApplicationService servicio;

    @BeforeEach
    void configurar() {
        servicio = new ClienteApplicationService(clienteRepository, eventPublisherPort, comunaCatalogPort);
    }

    @Test
    @DisplayName("Crear cliente exitosamente publica eventos y retorna un ClienteId")
    void crearClienteExitoso() {
        when(clienteRepository.existePorRut(any(Rut.class))).thenReturn(false);
        when(clienteRepository.guardar(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteId id = servicio.ejecutar(new CrearClienteUseCase.Comando(
                "12345678-5", TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of("+56912345678"), List.of(), CondicionPago.CONTADO,
                PrioridadComercial.ESTANDAR, "admin"));

        assertThat(id).isNotNull();
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).guardar(captor.capture());
        assertThat(captor.getValue().rut().formatoCanonico()).isEqualTo("12345678-5");
        verify(eventPublisherPort).publicar(any());
    }

    @Test
    @DisplayName("Crear cliente con RUT ya registrado lanza RutDuplicadoException y no persiste")
    void crearClienteConRutDuplicado() {
        when(clienteRepository.existePorRut(any(Rut.class))).thenReturn(true);

        assertThatThrownBy(() -> servicio.ejecutar(new CrearClienteUseCase.Comando(
                "12345678-5", TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of("+56912345678"), List.of(), CondicionPago.CONTADO,
                PrioridadComercial.ESTANDAR, "admin")))
                .isInstanceOf(RutDuplicadoException.class);

        verify(clienteRepository, never()).guardar(any());
        verify(eventPublisherPort, never()).publicar(any());
    }

    @Test
    @DisplayName("Consultar un cliente inexistente lanza ClienteNoEncontradoException")
    void consultarClienteInexistente() {
        ClienteId id = ClienteId.nuevo();
        when(clienteRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerPorId(id.value().toString()))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    @DisplayName("Agregar direccion con comuna desconocida lanza ComunaDesconocidaException")
    void agregarDireccionConComunaDesconocida() {
        Cliente cliente = clienteExistente();
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(comunaCatalogPort.buscarPorCodigo("NO_EXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.ejecutar(new AgregarDireccionUseCase.Comando(
                cliente.id().value().toString(), "Calle", "1", "NO_EXISTE", -33.0, -70.0,
                null, false, "admin")))
                .isInstanceOf(ComunaDesconocidaException.class);
    }

    @Test
    @DisplayName("Agregar direccion exitosa retorna el id de la nueva direccion")
    void agregarDireccionExitosa() {
        Cliente cliente = clienteExistente();
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(comunaCatalogPort.buscarPorCodigo("PUENTE_ALTO")).thenReturn(Optional.of(PUENTE_ALTO));
        when(clienteRepository.guardar(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        DireccionId direccionId = servicio.ejecutar(new AgregarDireccionUseCase.Comando(
                cliente.id().value().toString(), "Los Aromos", "123", "PUENTE_ALTO", -33.6, -70.6,
                "Casa azul", false, "admin"));

        assertThat(direccionId).isNotNull();
        assertThat(cliente.direcciones()).hasSize(1);
    }

    @Test
    @DisplayName("Desactivar cliente delega en el agregado y persiste el cambio")
    void desactivarCliente() {
        Cliente cliente = clienteExistente();
        when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
        when(clienteRepository.guardar(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.ejecutar(new DesactivarClienteUseCase.Comando(cliente.id().value().toString(), "admin"));

        assertThat(cliente.estado()).isEqualTo(EstadoCliente.INACTIVO);
        verify(clienteRepository).guardar(cliente);
    }

    private static Cliente clienteExistente() {
        Cliente cliente = Cliente.crear(
                Rut.of("12345678-5"), TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of(new cl.smartlogistic.customer.domain.model.Telefono("+56912345678")), List.of(),
                CondicionPago.CONTADO, PrioridadComercial.ESTANDAR, "admin", Instant.now());
        cliente.eventosPendientes();
        return cliente;
    }
}
