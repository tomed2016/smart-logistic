package cl.smartlogistic.customer.domain.model;

import cl.smartlogistic.customer.domain.exception.ClienteSinContactoException;
import cl.smartlogistic.customer.domain.exception.DireccionNoEncontradaException;
import cl.smartlogistic.customer.domain.event.ClienteActualizadoEvent;
import cl.smartlogistic.customer.domain.event.ClienteCreadoEvent;
import cl.smartlogistic.customer.domain.event.ClienteDesactivadoEvent;
import cl.smartlogistic.customer.domain.event.DireccionAgregadaEvent;
import cl.smartlogistic.shared.audit.AuditInfo;
import cl.smartlogistic.shared.event.DomainEvent;
import cl.smartlogistic.shared.rut.Rut;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Agregado raiz del bounded context Clientes. Encapsula todas las invariantes de
 * negocio descritas en {@code docs/architecture/03-bounded-context-clientes.md}.
 *
 * <p>Los eventos de dominio generados por cada operacion se acumulan en una lista
 * interna y son recuperados (y limpiados) por la capa de aplicacion mediante
 * {@link #eventosPendientes()} para persistirlos en la tabla outbox dentro de la
 * misma transaccion del cambio de estado (patron Transactional Outbox, ADR-002).</p>
 */
public final class Cliente {

    private final ClienteId id;
    private final Rut rut;
    private final TipoCliente tipoCliente;
    private String nombre;
    private List<Telefono> telefonos;
    private List<Correo> correos;
    private CondicionPago condicionPago;
    private PrioridadComercial prioridadComercial;
    private EstadoCliente estado;
    private final List<Direccion> direcciones;
    private AuditInfo auditInfo;

    private final transient List<DomainEvent> eventos = new ArrayList<>();

    private Cliente(ClienteId id, Rut rut, TipoCliente tipoCliente, String nombre,
                     List<Telefono> telefonos, List<Correo> correos, CondicionPago condicionPago,
                     PrioridadComercial prioridadComercial, EstadoCliente estado,
                     List<Direccion> direcciones, AuditInfo auditInfo) {
        this.id = Objects.requireNonNull(id);
        this.rut = Objects.requireNonNull(rut);
        this.tipoCliente = Objects.requireNonNull(tipoCliente);
        this.nombre = requireNoBlank(nombre, "nombre");
        this.telefonos = new ArrayList<>(telefonos);
        this.correos = new ArrayList<>(correos);
        this.condicionPago = Objects.requireNonNull(condicionPago);
        this.prioridadComercial = Objects.requireNonNull(prioridadComercial);
        this.estado = Objects.requireNonNull(estado);
        this.direcciones = new ArrayList<>(direcciones);
        this.auditInfo = Objects.requireNonNull(auditInfo);
    }

    /**
     * Crea un nuevo cliente. Invariante: debe tener al menos un telefono o correo de
     * contacto para poder registrarse como {@code ACTIVO}.
     */
    public static Cliente crear(Rut rut, TipoCliente tipoCliente, String nombre,
                                 List<Telefono> telefonos, List<Correo> correos,
                                 CondicionPago condicionPago, PrioridadComercial prioridadComercial,
                                 String usuarioCreador, Instant ahora) {
        if (telefonos.isEmpty() && correos.isEmpty()) {
            throw new ClienteSinContactoException();
        }
        ClienteId id = ClienteId.nuevo();
        Cliente cliente = new Cliente(id, rut, tipoCliente, nombre, telefonos, correos, condicionPago,
                prioridadComercial, EstadoCliente.ACTIVO, List.of(), AuditInfo.crear(usuarioCreador, ahora));
        cliente.eventos.add(new ClienteCreadoEvent(id.value().toString(), rut.formatoCanonico()));
        return cliente;
    }

    /** Reconstruccion desde persistencia: no dispara eventos de dominio. */
    public static Cliente reconstruir(ClienteId id, Rut rut, TipoCliente tipoCliente, String nombre,
                                       List<Telefono> telefonos, List<Correo> correos,
                                       CondicionPago condicionPago, PrioridadComercial prioridadComercial,
                                       EstadoCliente estado, List<Direccion> direcciones, AuditInfo auditInfo) {
        return new Cliente(id, rut, tipoCliente, nombre, telefonos, correos, condicionPago,
                prioridadComercial, estado, direcciones, auditInfo);
    }

    public void actualizarDatosDeContacto(String nombre, List<Telefono> telefonos, List<Correo> correos,
                                           CondicionPago condicionPago, PrioridadComercial prioridadComercial,
                                           String usuario, Instant ahora) {
        if (telefonos.isEmpty() && correos.isEmpty()) {
            throw new ClienteSinContactoException();
        }
        this.nombre = requireNoBlank(nombre, "nombre");
        this.telefonos = new ArrayList<>(telefonos);
        this.correos = new ArrayList<>(correos);
        this.condicionPago = Objects.requireNonNull(condicionPago);
        this.prioridadComercial = Objects.requireNonNull(prioridadComercial);
        this.auditInfo = auditInfo.actualizar(usuario, ahora);
        eventos.add(new ClienteActualizadoEvent(id.value().toString()));
    }

    /**
     * Agrega una nueva direccion. Invariante: existe exactamente una direccion
     * principal. Si es la primera direccion del cliente, se marca automaticamente
     * como principal sin importar el valor solicitado; si se solicita explicitamente
     * como principal y ya existe otra, esta se desmarca.
     */
    public DireccionId agregarDireccion(String calle, String numero, Comuna comuna, Coordenadas coordenadas,
                                         String referencia, boolean marcarComoPrincipal,
                                         String usuario, Instant ahora) {
        boolean esPrimeraDireccion = direcciones.isEmpty();
        boolean seraPrincipal = esPrimeraDireccion || marcarComoPrincipal;

        if (seraPrincipal) {
            direcciones.forEach(Direccion::desmarcarComoPrincipal);
        }

        Direccion nueva = Direccion.crear(calle, numero, comuna, coordenadas, referencia, seraPrincipal);
        direcciones.add(nueva);
        this.auditInfo = auditInfo.actualizar(usuario, ahora);
        eventos.add(new DireccionAgregadaEvent(id.value().toString(), nueva.id().value().toString()));
        return nueva.id();
    }

    public void actualizarDireccion(DireccionId direccionId, String calle, String numero, Comuna comuna,
                                     Coordenadas coordenadas, String referencia, String usuario, Instant ahora) {
        Direccion direccion = buscarDireccion(direccionId);
        direccion.actualizarDatos(calle, numero, comuna, coordenadas, referencia);
        this.auditInfo = auditInfo.actualizar(usuario, ahora);
    }

    public void marcarDireccionComoPrincipal(DireccionId direccionId, String usuario, Instant ahora) {
        Direccion direccion = buscarDireccion(direccionId);
        direcciones.forEach(Direccion::desmarcarComoPrincipal);
        direccion.marcarComoPrincipal();
        this.auditInfo = auditInfo.actualizar(usuario, ahora);
    }

    private Direccion buscarDireccion(DireccionId direccionId) {
        return direcciones.stream()
                .filter(d -> d.id().equals(direccionId))
                .findFirst()
                .orElseThrow(() -> new DireccionNoEncontradaException(direccionId.value().toString()));
    }

    public void desactivar(String usuario, Instant ahora) {
        this.estado = EstadoCliente.INACTIVO;
        this.auditInfo = auditInfo.actualizar(usuario, ahora);
        eventos.add(new ClienteDesactivadoEvent(id.value().toString()));
    }

    public void reactivar(String usuario, Instant ahora) {
        if (telefonos.isEmpty() && correos.isEmpty()) {
            throw new ClienteSinContactoException();
        }
        this.estado = EstadoCliente.ACTIVO;
        this.auditInfo = auditInfo.actualizar(usuario, ahora);
    }

    /** Devuelve y limpia los eventos de dominio acumulados (uso exclusivo de la capa de aplicacion). */
    public List<DomainEvent> eventosPendientes() {
        List<DomainEvent> copia = List.copyOf(eventos);
        eventos.clear();
        return copia;
    }

    private static String requireNoBlank(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar en blanco");
        }
        return valor;
    }

    public ClienteId id() {
        return id;
    }

    public Rut rut() {
        return rut;
    }

    public TipoCliente tipoCliente() {
        return tipoCliente;
    }

    public String nombre() {
        return nombre;
    }

    public List<Telefono> telefonos() {
        return List.copyOf(telefonos);
    }

    public List<Correo> correos() {
        return List.copyOf(correos);
    }

    public CondicionPago condicionPago() {
        return condicionPago;
    }

    public PrioridadComercial prioridadComercial() {
        return prioridadComercial;
    }

    public EstadoCliente estado() {
        return estado;
    }

    public List<Direccion> direcciones() {
        return List.copyOf(direcciones);
    }

    public Optional<Direccion> direccionPrincipal() {
        return direcciones.stream().filter(Direccion::esPrincipal).findFirst();
    }

    public AuditInfo auditInfo() {
        return auditInfo;
    }
}
