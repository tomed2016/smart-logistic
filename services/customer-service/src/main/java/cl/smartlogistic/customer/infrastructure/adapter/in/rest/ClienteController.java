package cl.smartlogistic.customer.infrastructure.adapter.in.rest;

import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.model.DireccionId;
import cl.smartlogistic.customer.domain.port.in.ActualizarClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.ActualizarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.AgregarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.ConsultarClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.CrearClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.DesactivarClienteUseCase;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.ActualizarClienteRequest;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.ActualizarDireccionRequest;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.AgregarDireccionRequest;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.ClienteResponse;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.CrearClienteRequest;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.IdResponse;
import cl.smartlogistic.customer.infrastructure.adapter.in.rest.dto.PaginaClientesResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada REST del bounded context Clientes (ver
 * {@code docs/architecture/03-bounded-context-clientes.md}).
 *
 * <p>El header {@code X-Usuario} identifica al usuario responsable del cambio para
 * fines de auditoria; en la Iteracion de Identidad y Acceso sera reemplazado por el
 * principal autenticado (JWT). Por ahora, si no se envia, se usa {@code "sistema"}.</p>
 */
@RestController
public class ClienteController {

    private static final String USUARIO_POR_DEFECTO = "sistema";

    private final CrearClienteUseCase crearClienteUseCase;
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final AgregarDireccionUseCase agregarDireccionUseCase;
    private final ActualizarDireccionUseCase actualizarDireccionUseCase;
    private final DesactivarClienteUseCase desactivarClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;

    public ClienteController(CrearClienteUseCase crearClienteUseCase,
                              ActualizarClienteUseCase actualizarClienteUseCase,
                              AgregarDireccionUseCase agregarDireccionUseCase,
                              ActualizarDireccionUseCase actualizarDireccionUseCase,
                              DesactivarClienteUseCase desactivarClienteUseCase,
                              ConsultarClienteUseCase consultarClienteUseCase) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.actualizarClienteUseCase = actualizarClienteUseCase;
        this.agregarDireccionUseCase = agregarDireccionUseCase;
        this.actualizarDireccionUseCase = actualizarDireccionUseCase;
        this.desactivarClienteUseCase = desactivarClienteUseCase;
        this.consultarClienteUseCase = consultarClienteUseCase;
    }

    @PostMapping("/api/v1/clientes")
    public ResponseEntity<IdResponse> crear(@Valid @RequestBody CrearClienteRequest request,
                                             @RequestParam(required = false) String usuario) {
        ClienteId id = crearClienteUseCase.ejecutar(new CrearClienteUseCase.Comando(
                request.rut(), request.tipoCliente(), request.nombre(), request.telefonos(),
                request.correos(), request.condicionPago(), request.prioridadComercial(),
                usuarioO(usuario)));
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id.value().toString()));
    }

    @GetMapping("/api/v1/clientes/{clienteId}")
    public ClienteResponse obtener(@PathVariable String clienteId) {
        return ClienteResponse.desde(consultarClienteUseCase.obtenerPorId(clienteId));
    }

    @GetMapping("/api/v1/clientes")
    public PaginaClientesResponse listar(@RequestParam(required = false) String comuna,
                                          @RequestParam(required = false) String estado,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return PaginaClientesResponse.desde(consultarClienteUseCase.listar(comuna, estado, page, size));
    }

    @PutMapping("/api/v1/clientes/{clienteId}")
    public ClienteResponse actualizar(@PathVariable String clienteId,
                                       @Valid @RequestBody ActualizarClienteRequest request,
                                       @RequestParam(required = false) String usuario) {
        actualizarClienteUseCase.ejecutar(new ActualizarClienteUseCase.Comando(
                clienteId, request.nombre(), request.telefonos(), request.correos(),
                request.condicionPago(), request.prioridadComercial(), usuarioO(usuario)));
        return ClienteResponse.desde(consultarClienteUseCase.obtenerPorId(clienteId));
    }

    @PostMapping("/api/v1/clientes/{clienteId}/direcciones")
    public ResponseEntity<IdResponse> agregarDireccion(@PathVariable String clienteId,
                                                        @Valid @RequestBody AgregarDireccionRequest request,
                                                        @RequestParam(required = false) String usuario) {
        DireccionId direccionId = agregarDireccionUseCase.ejecutar(new AgregarDireccionUseCase.Comando(
                clienteId, request.calle(), request.numero(), request.codigoComuna(),
                request.latitud(), request.longitud(), request.referencia(), request.marcarComoPrincipal(),
                usuarioO(usuario)));
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(direccionId.value().toString()));
    }

    @PutMapping("/api/v1/clientes/{clienteId}/direcciones/{direccionId}")
    public ClienteResponse actualizarDireccion(@PathVariable String clienteId,
                                                @PathVariable String direccionId,
                                                @Valid @RequestBody ActualizarDireccionRequest request,
                                                @RequestParam(required = false) String usuario) {
        actualizarDireccionUseCase.ejecutar(new ActualizarDireccionUseCase.Comando(
                clienteId, direccionId, request.calle(), request.numero(), request.codigoComuna(),
                request.latitud(), request.longitud(), request.referencia(), usuarioO(usuario)));
        return ClienteResponse.desde(consultarClienteUseCase.obtenerPorId(clienteId));
    }

    @DeleteMapping("/api/v1/clientes/{clienteId}")
    public ResponseEntity<Void> desactivar(@PathVariable String clienteId,
                                            @RequestParam(required = false) String usuario) {
        desactivarClienteUseCase.ejecutar(new DesactivarClienteUseCase.Comando(clienteId, usuarioO(usuario)));
        return ResponseEntity.noContent().build();
    }

    private static String usuarioO(String usuario) {
        return (usuario == null || usuario.isBlank()) ? USUARIO_POR_DEFECTO : usuario;
    }
}
