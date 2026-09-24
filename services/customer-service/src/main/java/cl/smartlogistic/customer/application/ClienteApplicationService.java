package cl.smartlogistic.customer.application;

import cl.smartlogistic.customer.domain.exception.ClienteNoEncontradoException;
import cl.smartlogistic.customer.domain.exception.ComunaDesconocidaException;
import cl.smartlogistic.customer.domain.exception.RutDuplicadoException;
import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.Coordenadas;
import cl.smartlogistic.customer.domain.model.Correo;
import cl.smartlogistic.customer.domain.model.DireccionId;
import cl.smartlogistic.customer.domain.model.Telefono;
import cl.smartlogistic.customer.domain.port.in.ActualizarClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.ActualizarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.AgregarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.ConsultarClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.CrearClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.DesactivarClienteUseCase;
import cl.smartlogistic.customer.domain.port.out.ClienteRepository;
import cl.smartlogistic.customer.domain.port.out.ComunaCatalogPort;
import cl.smartlogistic.customer.domain.port.out.EventPublisherPort;
import cl.smartlogistic.shared.rut.Rut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Servicio de aplicacion que orquesta los casos de uso del bounded context Clientes.
 * Coordina el agregado {@link Cliente}, la persistencia y la publicacion de eventos
 * de dominio, pero no contiene reglas de negocio (estas viven en el propio agregado).
 *
 * <p>Se acepta como excepcion pragmatica al principio de "dominio/aplicacion libres de
 * framework" el uso de anotaciones de Spring ({@code @Service}, {@code @Transactional})
 * en esta clase: delimitar la frontera transaccional es una responsabilidad propia de
 * la capa de aplicacion, y cada microservicio ya esta acoplado a Spring Boot como
 * runtime (no se busca portabilidad de framework, sino separacion de responsabilidades
 * y testabilidad del dominio).</p>
 */
@Service
@Transactional
public class ClienteApplicationService implements
        CrearClienteUseCase, ActualizarClienteUseCase, AgregarDireccionUseCase, ActualizarDireccionUseCase,
        DesactivarClienteUseCase, ConsultarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final EventPublisherPort eventPublisherPort;
    private final ComunaCatalogPort comunaCatalogPort;

    public ClienteApplicationService(ClienteRepository clienteRepository,
                                      EventPublisherPort eventPublisherPort,
                                      ComunaCatalogPort comunaCatalogPort) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
        this.eventPublisherPort = Objects.requireNonNull(eventPublisherPort);
        this.comunaCatalogPort = Objects.requireNonNull(comunaCatalogPort);
    }

    @Override
    public ClienteId ejecutar(CrearClienteUseCase.Comando comando) {
        Rut rut = Rut.of(comando.rut());
        if (clienteRepository.existePorRut(rut)) {
            throw new RutDuplicadoException(rut.formatoCanonico());
        }
        Instant ahora = Instant.now();
        Cliente cliente = Cliente.crear(
                rut,
                comando.tipoCliente(),
                comando.nombre(),
                aTelefonos(comando.telefonos()),
                aCorreos(comando.correos()),
                comando.condicionPago(),
                comando.prioridadComercial(),
                comando.usuario(),
                ahora);
        Cliente guardado = clienteRepository.guardar(cliente);
        eventPublisherPort.publicar(guardado.eventosPendientes());
        return guardado.id();
    }

    @Override
    public void ejecutar(ActualizarClienteUseCase.Comando comando) {
        Cliente cliente = obtenerAgregado(comando.clienteId());
        cliente.actualizarDatosDeContacto(
                comando.nombre(),
                aTelefonos(comando.telefonos()),
                aCorreos(comando.correos()),
                comando.condicionPago(),
                comando.prioridadComercial(),
                comando.usuario(),
                Instant.now());
        Cliente guardado = clienteRepository.guardar(cliente);
        eventPublisherPort.publicar(guardado.eventosPendientes());
    }

    @Override
    public DireccionId ejecutar(AgregarDireccionUseCase.Comando comando) {
        Cliente cliente = obtenerAgregado(comando.clienteId());
        Comuna comuna = comunaCatalogPort.buscarPorCodigo(comando.codigoComuna())
                .orElseThrow(() -> new ComunaDesconocidaException(comando.codigoComuna()));
        DireccionId direccionId = cliente.agregarDireccion(
                comando.calle(),
                comando.numero(),
                comuna,
                new Coordenadas(comando.latitud(), comando.longitud()),
                comando.referencia(),
                comando.marcarComoPrincipal(),
                comando.usuario(),
                Instant.now());
        Cliente guardado = clienteRepository.guardar(cliente);
        eventPublisherPort.publicar(guardado.eventosPendientes());
        return direccionId;
    }

    @Override
    public void ejecutar(ActualizarDireccionUseCase.Comando comando) {
        Cliente cliente = obtenerAgregado(comando.clienteId());
        Comuna comuna = comunaCatalogPort.buscarPorCodigo(comando.codigoComuna())
                .orElseThrow(() -> new ComunaDesconocidaException(comando.codigoComuna()));
        cliente.actualizarDireccion(
                DireccionId.de(comando.direccionId()),
                comando.calle(),
                comando.numero(),
                comuna,
                new Coordenadas(comando.latitud(), comando.longitud()),
                comando.referencia(),
                comando.usuario(),
                Instant.now());
        Cliente guardado = clienteRepository.guardar(cliente);
        eventPublisherPort.publicar(guardado.eventosPendientes());
    }

    @Override
    public void ejecutar(DesactivarClienteUseCase.Comando comando) {
        Cliente cliente = obtenerAgregado(comando.clienteId());
        cliente.desactivar(comando.usuario(), Instant.now());
        Cliente guardado = clienteRepository.guardar(cliente);
        eventPublisherPort.publicar(guardado.eventosPendientes());
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(String clienteId) {
        return obtenerAgregado(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteRepository.PaginaClientes listar(String codigoComuna, String estado, int pagina, int tamanoPagina) {
        return clienteRepository.buscar(new ClienteRepository.FiltroClientes(codigoComuna, estado), pagina, tamanoPagina);
    }

    private Cliente obtenerAgregado(String clienteId) {
        return clienteRepository.buscarPorId(ClienteId.de(clienteId))
                .orElseThrow(() -> new ClienteNoEncontradoException(clienteId));
    }

    private static List<Telefono> aTelefonos(List<String> valores) {
        return valores.stream().map(Telefono::new).toList();
    }

    private static List<Correo> aCorreos(List<String> valores) {
        return valores.stream().map(Correo::new).toList();
    }
}
